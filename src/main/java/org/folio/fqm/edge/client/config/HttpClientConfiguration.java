package org.folio.fqm.edge.client.config;

import org.folio.fqm.edge.client.EntityTypesClient;
import org.folio.fqm.edge.client.QueryClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration class to register HTTP service clients.
 *
 * <p>Two HttpServiceProxyFactory beans exist:
 * <ul>
 *   <li>{@code httpServiceProxyFactory} — from folio-spring-base (includes Okapi header enrichment)</li>
 *   <li>{@code edgeHttpServiceProxyFactory} — from edge-common-spring</li>
 * </ul>
 *
 * We mark {@code httpServiceProxyFactory} as {@code @Primary} so that internal folio-spring-base beans
 * (e.g. {@code authnClient}) that inject an unqualified {@code HttpServiceProxyFactory} resolve correctly.
 * Our edge clients also use the primary factory.
 */
@Configuration
public class HttpClientConfiguration {

  @Bean
  @Primary
  public HttpServiceProxyFactory primaryHttpServiceProxyFactory(
      @Qualifier("httpServiceProxyFactory") HttpServiceProxyFactory factory) {
    return factory;
  }

  @Bean
  public QueryClient queryClient(@Qualifier("httpServiceProxyFactory") HttpServiceProxyFactory factory) {
    return factory.createClient(QueryClient.class);
  }

  @Bean
  public EntityTypesClient entityTypesClient(@Qualifier("httpServiceProxyFactory") HttpServiceProxyFactory factory) {
    return factory.createClient(EntityTypesClient.class);
  }
}
