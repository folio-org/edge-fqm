package org.folio.fqm.edge.client.config;

import org.folio.fqm.edge.client.EntityTypesClient;
import org.folio.fqm.edge.client.QueryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration class to register HTTP service clients.
 * The HttpServiceProxyFactory bean is provided by folio-spring-base.
 * All clients automatically use the EnrichUrlAndHeadersInterceptor for Okapi headers.
 */
@Configuration
public class HttpClientConfiguration {

  @Bean
  public QueryClient queryClient(HttpServiceProxyFactory factory) {
    return factory.createClient(QueryClient.class);
  }

  @Bean
  public EntityTypesClient entityTypesClient(HttpServiceProxyFactory factory) {
    return factory.createClient(EntityTypesClient.class);
  }
}

