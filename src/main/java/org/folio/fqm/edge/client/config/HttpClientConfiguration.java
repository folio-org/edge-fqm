package org.folio.fqm.edge.client.config;

import org.folio.fqm.edge.client.EntityTypesClient;
import org.folio.fqm.edge.client.QueryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration for HTTP service clients in this edge module.
 *
 * The {@code HttpServiceProxyFactory} is provided by {@code edge-common-spring}, which
 * resolves the Okapi base URL from {@code EdgeClientProperties} and propagates headers from
 * {@code FolioExecutionContext}. Each client interface just needs a bean definition here.
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
