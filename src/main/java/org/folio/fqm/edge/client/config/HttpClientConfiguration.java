package org.folio.fqm.edge.client.config;

import org.folio.fqm.edge.client.EntityTypesClient;
import org.folio.fqm.edge.client.QueryClient;
import org.folio.edgecommonspring.client.EdgeClientProperties;
import org.folio.spring.DefaultFolioExecutionContext;
import org.folio.spring.FolioModuleMetadata;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.spring.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Qualifier;
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

import lombok.extern.log4j.Log4j2;

/**
 * Configuration class to register HTTP service clients for this edge module.
 *
 * <p>Builds a custom {@link RestClient} with an interceptor that propagates Okapi headers
 * (tenant, token, user-id) from the incoming request to outgoing HTTP service client calls,
 * and dynamically resolves the base URL from {@link EdgeClientProperties} at request time.
 * This replaces the old Feign-based {@code OkapiFeignClientConfig}.
 *
 * <p>There are multiple {@link HttpServiceProxyFactory} beans in the context:
 * <ul>
 *   <li>{@code httpServiceProxyFactory} - from folio-spring-base, used by internal clients like {@code authnClient}</li>
 *   <li>{@code edgeHttpServiceProxyFactory} - from edge-common-spring</li>
 *   <li>{@code fqmHttpServiceProxyFactory} - defined here, used by this module's clients</li>
 * </ul>
 * We mark {@code httpServiceProxyFactory} as {@code @Primary} so that folio-spring-base's internal
 * beans resolve correctly, and explicitly wire our module's clients to {@code fqmHttpServiceProxyFactory}.
 */
@Configuration
@Log4j2
public class HttpClientConfiguration {

  /**
   * Marks the folio-spring-base {@code httpServiceProxyFactory} as primary so that internal
   * beans like {@code authnClient} (which inject an unqualified HttpServiceProxyFactory)
   * resolve correctly to the standard folio factory, not the edge-specific one.
   */
  @Bean
  @Primary
  public HttpServiceProxyFactory primaryHttpServiceProxyFactory(
      @Qualifier("httpServiceProxyFactory") HttpServiceProxyFactory factory) {
    return factory;
  }

  @Bean
  public HttpServiceProxyFactory fqmHttpServiceProxyFactory(
      EdgeClientProperties edgeClientProperties,
      FolioModuleMetadata folioModuleMetadata) {

    ClientHttpRequestInterceptor okapiHeadersInterceptor = (request, body, execution) -> {
      var context = new DefaultFolioExecutionContext(folioModuleMetadata, RequestUtils.getHttpHeadersFromRequest());
      HttpHeaders headers = request.getHeaders();
      addHeaderIfPresent(headers, XOkapiHeaders.TENANT, context.getTenantId());
      addHeaderIfPresent(headers, XOkapiHeaders.TOKEN, context.getToken());
      String userId = context.getUserId() != null ? context.getUserId().toString() : null;
      addHeaderIfPresent(headers, XOkapiHeaders.USER_ID, userId);
      log.debug("Outgoing request: {} {}, tenant={}, token={}, userId={}",
          request.getMethod(), request.getURI(),
          context.getTenantId(),
          context.getToken() != null ? context.getToken().substring(0, Math.min(10, context.getToken().length())) + "..." : "null",
          userId);
      return execution.execute(request, body);
    };

    ClientHttpRequestInterceptor baseUrlInterceptor = (request, body, execution) -> {
      String baseUrl = edgeClientProperties.getOkapiUrl();
      URI original = request.getURI();
      URI resolved = URI.create(baseUrl).resolve(original.getRawPath()
          + (original.getRawQuery() != null ? "?" + original.getRawQuery() : ""));
      log.debug("Base URL interceptor: {} -> {}", original, resolved);
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
  public QueryClient queryClient(@Qualifier("fqmHttpServiceProxyFactory") HttpServiceProxyFactory factory) {
    return factory.createClient(QueryClient.class);
  }

  @Bean
  public EntityTypesClient entityTypesClient(@Qualifier("fqmHttpServiceProxyFactory") HttpServiceProxyFactory factory) {
    return factory.createClient(EntityTypesClient.class);
  }

  private static void addHeaderIfPresent(HttpHeaders headers, String name, String value) {
    if (value != null) {
      headers.add(name, value);
    }
  }
}
