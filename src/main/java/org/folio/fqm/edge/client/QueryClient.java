package org.folio.fqm.edge.client;

import jakarta.validation.constraints.NotNull;
import org.folio.querytool.domain.dto.ContentsRequest;
import org.folio.querytool.domain.dto.QueryDetails;
import org.folio.querytool.domain.dto.QueryIdentifier;
import org.folio.querytool.domain.dto.ResultsetPage;
import org.folio.querytool.domain.dto.SubmitQuery;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@HttpExchange(url = "query")
public interface QueryClient {

  @GetExchange
  ResultsetPage runFqlQuery(@RequestParam @NotNull String query,
                            @RequestParam @NotNull UUID entityTypeId,
                            @RequestParam(required = false) List<String> fields,
                            @RequestParam(required = false) Integer limit);

  @DeleteExchange(url = "/{queryId}")
  void deleteQuery(@PathVariable UUID queryId);

  @GetExchange(url = "/{queryId}")
  QueryDetails getQuery(@PathVariable UUID queryId,
                        @RequestParam(required = false) Boolean includeResults,
                        @RequestParam(required = false) Integer offset,
                        @RequestParam(required = false) Integer limit);

  @PostExchange
  QueryIdentifier runFqlQueryAsync(@RequestBody SubmitQuery submitQuery);

  @PostExchange(url = "/contents")
  List<Map<String, Object>> getContents(@RequestBody ContentsRequest contentsRequest);

  @GetExchange(url = "/{queryId}/sortedIds")
  List<List<String>> getSortedIds(@PathVariable UUID queryId,
                          @RequestParam(required = false) Integer offset,
                          @RequestParam(required = false) Integer limit);
}
