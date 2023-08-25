package org.folio.fqm.edge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EdgeFqmApplicationTest {

  @Test
  void appShouldStartSuccessfully() {
    assertTrue(true);
  }

}
