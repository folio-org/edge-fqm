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
 * <p>In an edge module, the base URL for all outgoing requests to Okapi comes from
 * {@link EdgeClientProperties#getOkapiUrl()}. The standard {@code httpServiceProxyFactory}
 * from folio-spring-base relies on {@code FolioExecutionContext} for the base URL, but that
 * context is not available during the {@code EdgeSecurityFilter} phase (when {@code authnClient}
 * is called to authenticate the system user).
 *
 * <p>This configuration provides:
 * <ul>
 *   <li>A {@code @Primary} factory with the base URL from {@code EdgeClientProperties} so that
 *       all beans (including {@code authnClient}) can resolve the Okapi URL.</li>
 *   <li>A separate {@code fqmHttpServiceProxyFactory} for this module's clients that also adds
 *       Okapi header propagation (tenant, token, user-id) from the incoming request.</li>
 * </ul>
 */
@Configuration
@Log4j2
public class HttpClientConfiguration {

  /**
   * A base URL interceptor that prepends the Okapi URL from {@link EdgeClientProperties} to all
   * outgoing requests. The URL is resolved lazily at request time so tests can change it dynamically.
   */
  private static ClientHttpRequestInterceptor createBaseUrlInterceptor(EdgeClientProperties edgeClientProperties) {
    return (request, body, execution) -> {
      String baseUrl = edgeClientProperties.getOkapiUrl();
      URI original = request.getURI();
      // Only prepend the base URL if the request URI doesn't already have a host
      if (original.getHost() != null) {
        return execution.execute(request, body);
      }
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
  }

  /**
   * Primary HttpServiceProxyFactory that includes the Okapi base URL from EdgeClientProperties.
   * This is used by all beans that inject an unqualified HttpServiceProxyFactory, including
   * folio-spring-base's {@code authnClient} (called during EdgeSecurityFilter before
   * FolioExecutionContext is available).
   */
  @Bean
  @Primary
  public HttpServiceProxyFactory primaryHttpServiceProxyFactory(EdgeClientProperties edgeClientProperties) {
    RestClient restClient = RestClient.builder()
        .requestInterceptor(createBaseUrlInterceptor(edgeClientProperties))
        .build();
    return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
  }

  /**
   * HttpServiceProxyFactory for this module's FQM clients. Includes both the base URL interceptor
   * and Okapi header propagation (tenant, token, user-id) from the incoming HTTP request.
   */
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
      log.debug("FQM client request: {} {}, tenant={}, token={}, userId={}",
          request.getMethod(), request.getURI(),
          context.getTenantId(),
          context.getToken() != null ? context.getToken().substring(0, Math.min(10, context.getToken().length())) + "..." : "null",
          userId);
      return execution.execute(request, body);
    };

    RestClient restClient = RestClient.builder()
        .requestInterceptor(createBaseUrlInterceptor(edgeClientProperties))
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
