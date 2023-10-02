package org.folio.fqm.edge.client.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exception handler for Feign client errors, passing them back to the edge API caller.
 * <p>
 * Overall flow here, since it's not very obvious: Errors from Feign clients configured with
 * {@link OkapiFeignClientConfig} are turned into instances of {@link OkapiFeignClientErrorWrapperException}. This class
 * then handles those, converting them into {@link ResponseEntity} objects that get returned to the original caller.
 */
@ControllerAdvice
public class OkapiFeignClientExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Set<String> allowedHeaders = Stream.of(
          HttpHeaders.ALLOW,
          HttpHeaders.MAX_FORWARDS,
          HttpHeaders.TRANSFER_ENCODING,
          HttpHeaders.CONTENT_DISPOSITION,
          HttpHeaders.CONTENT_LANGUAGE,
          HttpHeaders.CONTENT_LENGTH,
          HttpHeaders.CONTENT_TYPE,
          HttpHeaders.DATE,
          HttpHeaders.ETAG,
          HttpHeaders.EXPIRES,
          HttpHeaders.LAST_MODIFIED,
          HttpHeaders.WARNING
      )
      .map(String::toLowerCase)
      .collect(Collectors.toSet());

  @ExceptionHandler(OkapiFeignClientErrorWrapperException.class)
  public ResponseEntity<Object> handleFeignError(OkapiFeignClientErrorWrapperException e) {
    var headers = new HttpHeaders();
    for (Map.Entry<String, Collection<String>> header : e.getHeaders().entrySet()) {
      // Copy any allowed headers
      if (allowedHeaders.contains(header.getKey().toLowerCase())) {
        headers.addAll(header.getKey(), new ArrayList<>(header.getValue()));
      }
    }

    return ResponseEntity
        .status(e.getCode())
        .headers(headers)
        .body(e.getBody());
  }
}
