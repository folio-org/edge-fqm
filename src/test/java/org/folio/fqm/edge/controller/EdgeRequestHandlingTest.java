package org.folio.fqm.edge.controller;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.folio.edge.core.utils.ApiKeyUtils;
import org.folio.edgecommonspring.client.AuthnClient;
import org.folio.edgecommonspring.client.EnrichUrlClient;
import org.folio.spring.integration.XOkapiHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class EdgeRequestHandlingTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EnrichUrlClient enrichUrlClient;

  @MockBean
  private AuthnClient authnClient;

  private MockWebServer mockFqmServer;

  @BeforeEach
  void setUp() throws IOException {
    // Set up a dummy response from the mock server so that QueryClient can make requests
    mockFqmServer = new MockWebServer();
    mockFqmServer.start();
    ReflectionTestUtils.setField(enrichUrlClient, "okapiUrl", "http://localhost:" + mockFqmServer.getPort());
  }

  @AfterEach
  void tearDown() throws IOException {
    mockFqmServer.shutdown();
  }

  @Test
  void shouldConvertApiKeyToHeaders() throws Exception {
    // Given
    String tenant = "diku",
      username = "diku",
      token = "This is totally a real token. For real!",
      query = "The best query";
    var entityTypeId = UUID.randomUUID().toString();
    var apiKey = ApiKeyUtils.generateApiKey(10, tenant, username);
    var responseBody = ""; // Arbitrary string. We don't care about the actual content and an empty string is easy
    setUpMockAuthnClient(tenant, token);

    // When we make a valid request to mod-fqm-manager with the API key set
    // Note: /query is an arbitrary API endpoint that does the API key -> header conversion and forwards the request
    mockFqmServer.enqueue(new MockResponse()
      .setResponseCode(200)
      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setBody(responseBody));
    var response = mockMvc.perform(get("/query?query={query}&entityTypeId={entityTypeId}&apiKey={apiKey}", query, entityTypeId, apiKey)
      .contentType(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    // Then the outgoing response from the edge API should contain the Okapi auth headers and the response body should
    // match mod-fqm-manager's response
    var headers = mockFqmServer.takeRequest().getHeaders();
    assertThat(headers.get(XOkapiHeaders.TENANT)).isEqualTo(tenant);
    assertThat(headers.get(XOkapiHeaders.TOKEN)).isEqualTo(token);
    assertThat(headers.get(XOkapiHeaders.USER_ID)).isNull();
    assertThat(response.getContentAsString()).isEqualTo(responseBody);
  }

  @Test
  void shouldReturnClientErrors() throws Exception {
    // Given
    String tenant = "diku",
      username = "diku",
      token = "This is totally a real token. For real!",
      query = "The best query";
    var entityTypeId = UUID.randomUUID().toString();
    var apiKey = ApiKeyUtils.generateApiKey(10, tenant, username);
    var fqmResponseCode = HttpStatus.I_AM_A_TEAPOT.value(); // Arbitrary HTTP error status code
    var fqmResponseBody = "I'm a teapot, not an FQM manager!";
    setUpMockAuthnClient(tenant, token);

    // When mod-fqm-manager responds with an error
    mockFqmServer.enqueue(new MockResponse()
      .setResponseCode(fqmResponseCode)
      .setBody(fqmResponseBody));
    var response = mockMvc.perform(get("/query?query={query}&entityTypeId={entityTypeId}&apiKey={apiKey}", query, entityTypeId, apiKey)
      .contentType(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    // Then the edge API response should contain the error message from mod-fqm-manager
    assertThat(response.getStatus()).isEqualTo(fqmResponseCode);
    assertThat(response.getContentAsString()).isEqualTo(fqmResponseBody);
  }

  private void setUpMockAuthnClient(String tenant, String token) {
    var responseHeaders = new HttpHeaders() {{
      add(XOkapiHeaders.TENANT, tenant);
      add(XOkapiHeaders.TOKEN, token);
    }};
    when(authnClient.getApiKey(any(), eq(tenant)))
      .thenReturn(new ResponseEntity<>(null, responseHeaders, HttpStatus.OK));
  }
}
