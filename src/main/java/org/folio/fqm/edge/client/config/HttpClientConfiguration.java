package org.folio.fqm.edge.client.config;

import org.folio.fqm.edge.client.EntityTypesClient;
import org.folio.fqm.edge.client.QueryClient;
import org.folio.edgecommonspring.client.EdgeClientProperties;
import org.folio.spring.DefaultFolioExecutionContext;
import org.folio.spring.FolioModuleMetadata;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.spring.utils.RequestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.URI;

/**
 * Configuration class to register HTTP service clients for this edge module.
 *
 * <p>Builds a custom {@link RestClient} with an interceptor that propagates Okapi headers
 * (tenant, token, user-id) from the incoming request to outgoing HTTP service client calls,
 * and dynamically resolves the base URL from {@link EdgeClientProperties} at request time.
 * This replaces the old Feign-based {@code OkapiFeignClientConfig}.
 */
@Configuration
public class HttpClientConfiguration {

  @Bean
  @Primary
  public HttpServiceProxyFactory fqmHttpServiceProxyFactory(
      EdgeClientProperties edgeClientProperties,
      FolioModuleMetadata folioModuleMetadata) {

    // Interceptor that adds Okapi headers at request time.
    ClientHttpRequestInterceptor okapiHeadersInterceptor = (request, body, execution) -> {
      var context = new DefaultFolioExecutionContext(folioModuleMetadata, RequestUtils.getHttpHeadersFromRequest());
      HttpHeaders headers = request.getHeaders();
      addHeaderIfPresent(headers, XOkapiHeaders.TENANT, context.getTenantId());
      addHeaderIfPresent(headers, XOkapiHeaders.TOKEN, context.getToken());
      String userId = context.getUserId() != null ? context.getUserId().toString() : null;
      addHeaderIfPresent(headers, XOkapiHeaders.USER_ID, userId);
      return execution.execute(request, body);
    };

    // Interceptor that prepends the base URL from EdgeClientProperties at request time.
    // This is resolved lazily so that tests can change the URL via ReflectionTestUtils.
    ClientHttpRequestInterceptor baseUrlInterceptor = (request, body, execution) -> {
      String baseUrl = edgeClientProperties.getOkapiUrl();
      URI original = request.getURI();
      URI resolved = URI.create(baseUrl).resolve(original.getRawPath()
          + (original.getRawQuery() != null ? "?" + original.getRawQuery() : ""));
      HttpRequest wrapped = new HttpRequestWrapper(request) {
        @Override
        public URI getURI() {
          return resolved;
        }
      };
      return execution.execute(wrapped, body);
    };

    RestClient restClient = RestClient.builder()
        .requestInterceptor(baseUrlInterceptor)
        .requestInterceptor(okapiHeadersInterceptor)
        .build();

    return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
  }

  @Bean
  public QueryClient queryClient(HttpServiceProxyFactory fqmHttpServiceProxyFactory) {
    return fqmHttpServiceProxyFactory.createClient(QueryClient.class);
  }

  @Bean
  public EntityTypesClient entityTypesClient(HttpServiceProxyFactory fqmHttpServiceProxyFactory) {
    return fqmHttpServiceProxyFactory.createClient(EntityTypesClient.class);
  }

  private static void addHeaderIfPresent(HttpHeaders headers, String name, String value) {
    if (value != null) {
      headers.add(name, value);
    }
  }
}
