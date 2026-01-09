package org.folio.fqm.edge.controller;

import lombok.RequiredArgsConstructor;
import org.folio.fqm.edge.service.QueryService;
import org.folio.querytool.domain.dto.ContentsRequest;
import org.folio.querytool.domain.dto.QueryDetails;
import org.folio.querytool.domain.dto.QueryIdentifier;
import org.folio.querytool.domain.dto.ResultsetPage;
import org.folio.querytool.domain.dto.SubmitQuery;
import org.folio.querytool.rest.resource.FqlQueryApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/", "/fqm"})
public class QueryController implements FqlQueryApi {

  private final QueryService queryService;

  @Override
  public ResponseEntity<ResultsetPage> runFqlQuery(String query, UUID entityTypeId, List<String> fields, List<String> afterId, Integer limit) {
    return ResponseEntity.ok(queryService.runFqlQuery(query, entityTypeId, fields, afterId, limit));
  }

  @Override
  public ResponseEntity<Void> deleteQuery(UUID queryId) {
    queryService.deleteQuery(queryId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<QueryDetails> getQuery(UUID queryId, Boolean includeResults, Integer offset, Integer limit) {
    return ResponseEntity.ok(queryService.getQuery(queryId, includeResults, offset, limit));
  }

  @Override
  public ResponseEntity<QueryIdentifier> runFqlQueryAsync(SubmitQuery submitQuery) {
    return new ResponseEntity<>(queryService.runFqlQueryAsync(submitQuery), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<List<Map<String, Object>>> getContents(ContentsRequest contentsRequest) {
    return ResponseEntity.ok(queryService.getContents(contentsRequest));
  }

  @Override
  public ResponseEntity<List<List<String>>> getSortedIds(UUID queryId, Integer offset, Integer limit) {
    return ResponseEntity.ok(queryService.getSortedIds(queryId, offset, limit));
  }
}
