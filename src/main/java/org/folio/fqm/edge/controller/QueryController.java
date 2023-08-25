package org.folio.fqm.edge.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.folio.fqm.edge.service.QueryService;
import org.folio.querytool.domain.dto.QueryDetails;
import org.folio.querytool.domain.dto.QueryIdentifier;
import org.folio.querytool.domain.dto.ResultsetPage;
import org.folio.querytool.domain.dto.SubmitQuery;
import org.folio.querytool.rest.resource.FqlQueryApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class QueryController implements FqlQueryApi {

  private final QueryService queryService;

  @Override
  public ResponseEntity<ResultsetPage> runFqlQuery(@NotNull String query, @NotNull UUID entityTypeId, List<String> fields, UUID afterId, Integer limit) {
    return ResponseEntity.ok(queryService.runFqlQuery(query, entityTypeId, fields, afterId, limit));
  }

  @Override
  public ResponseEntity<Void> deleteQuery(UUID queryId) {
    queryService.deleteQuery(queryId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<QueryDetails> getQuery(UUID queryId, @Valid Boolean includeResults, @Min(0L) @Max(2147483647L) @Valid Integer offset, @Min(1L) @Max(2147483647L) @Valid Integer limit) {
    return ResponseEntity.ok(queryService.getQuery(queryId, includeResults, offset, limit));
  }

  @Override
  public ResponseEntity<QueryIdentifier> runFqlQueryAsync(@Valid SubmitQuery submitQuery) {
    return ResponseEntity.ok(queryService.runFqlQueryAsync(submitQuery));
  }
}
