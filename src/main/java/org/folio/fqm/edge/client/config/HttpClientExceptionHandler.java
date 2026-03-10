package org.folio.fqm.edge.client.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exception handler for HTTP service client errors, passing them back to the edge API caller.

 * Errors from Spring HTTP Service Clients are thrown as {@link HttpStatusCodeException} instances.
 * This class handles those, converting them into {@link ResponseEntity} objects that get returned
 * to the original caller, while filtering headers.

 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpClientExceptionHandler {

  private static final Set<String> allowedHeaders = Stream.of(
          HttpHeaders.ALLOW,
          HttpHeaders.CACHE_CONTROL,
          HttpHeaders.CONTENT_DISPOSITION,
          HttpHeaders.CONTENT_LANGUAGE,
          HttpHeaders.CONTENT_TYPE,
          HttpHeaders.DATE,
          HttpHeaders.ETAG,
          HttpHeaders.EXPIRES,
          HttpHeaders.LAST_MODIFIED,
          HttpHeaders.RETRY_AFTER
      )
      .map(String::toLowerCase)
      .collect(Collectors.toSet());

  @ExceptionHandler(HttpStatusCodeException.class)
  public ResponseEntity<String> handleHttpStatusError(HttpStatusCodeException e) {
    HttpHeaders filteredHeaders = new HttpHeaders();
    HttpHeaders responseHeaders = e.getResponseHeaders();
    if (responseHeaders != null) {
      responseHeaders.forEach((name, values) -> {
        if (allowedHeaders.contains(name.toLowerCase())) {
          filteredHeaders.addAll(name, values);
        }
      });
    }

    return ResponseEntity
        .status(e.getStatusCode())
        .headers(filteredHeaders)
        .body(e.getResponseBodyAsString());
  }
}
