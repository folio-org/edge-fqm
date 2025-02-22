package org.folio.fqm.edge.controller;

import com.flextrade.jfixture.JFixture;
import org.folio.fqm.edge.client.EntityTypesClient;
import org.folio.fqm.edge.client.QueryClient;
import org.folio.fqm.edge.service.EntityTypesService;
import org.folio.fqm.edge.service.QueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EndpointPassThroughTest {

  @Autowired
  private EntityTypesController entityTypesController;
  @SpyBean
  private EntityTypesService entityTypesService;
  @MockBean
  private EntityTypesClient entityTypesClient;

  @Autowired
  private QueryController queryController;
  @SpyBean
  private QueryService queryService;
  @MockBean
  private QueryClient queryClient;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testEntityTypesPassThrough() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    // Skip getRequests() in the entity type controller, since it's only there to resolve a name conflict in its two parent interfaces
    // Skip getEntityTypeSummary() since it's not yet mirrored by edge-fqm (see EDGFQM-26)
    // TODO: Add getEntityTypeSummary() back in once it's mirrored by edge-fqm
    verifyPassThrough(EntityTypesController.class, entityTypesController, entityTypesService, entityTypesClient, "getRequest");
  }

  @Test
  void testQueryPassThrough() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    verifyPassThrough(QueryController.class, queryController, queryService, queryClient);
  }

  /**
   * Verify that all methods in controller pass straight through to the service and client.
   * <p>
   * Note: There's a build-in assumption here that the controller, service, and client have the same basic interface; as
   * in, if a method exists in the controller, then both the corresponding service and client should have methods with the
   * same signature.
   *
   * @param controllerClass - The concrete class for the controller; this is needed due to Spring's tendency to wrap classes, making it difficult to see what methods are actually overridden
   * @param controller      - The controller to verify
   * @param service         - The backing service for the controller
   * @param client          - The underlying client that the service uses to retrieve data
   * @param methodsToIgnore - Method names to skip
   */
  private <C, S, R> void verifyPassThrough(Class<C> controllerClass, C controller,
                                           S service,
                                           R client, String... methodsToIgnore) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    var ignoredMethods = Set.of(methodsToIgnore);
    for (var controllerMethod : controllerClass.getMethods()) {
      // Skip any non - overridden inherited methods, along with any explicitly skipped ones
      if (!controllerMethod.getDeclaringClass().equals(controllerClass) || ignoredMethods.contains(controllerMethod.getName())) {
        continue;
      }
      // Given a controller, service, and client method
      var serviceMethod = service.getClass().getMethod(controllerMethod.getName(), controllerMethod.getParameterTypes());
      var clientMethod = client.getClass().getMethod(controllerMethod.getName(), controllerMethod.getParameterTypes());

      // And given a set of random method arguments for the controller method
      JFixture fixture = new JFixture();
      var args = Stream.of(controllerMethod.getParameterTypes()).map(fixture::create).toArray();

      // When we call the controller method with those arguments
      controllerMethod.invoke(controller, args);

      // Then the service and client methods should get called with the same arguments exactly once
      serviceMethod.invoke(verify(service, times(1)), args);
      clientMethod.invoke(verify(client, times(1)), args);
    }
  }
}
