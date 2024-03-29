package org.folio.fqm.edge.client;

import jakarta.validation.constraints.NotNull;
import org.folio.fqm.edge.client.config.OkapiFeignClientConfig;
import org.folio.querytool.domain.dto.ContentsRequest;
import org.folio.querytool.domain.dto.QueryDetails;
import org.folio.querytool.domain.dto.QueryIdentifier;
import org.folio.querytool.domain.dto.ResultsetPage;
import org.folio.querytool.domain.dto.SubmitQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "query", configuration = OkapiFeignClientConfig.class)
public interface QueryClient {

  @GetMapping
  ResultsetPage runFqlQuery(@RequestParam @NotNull String query,
                            @RequestParam @NotNull UUID entityTypeId,
                            @RequestParam List<String> fields,
                            @RequestParam List<String> afterId,
                            @RequestParam Integer limit);

  @DeleteMapping(path = "/{queryId}")
  void deleteQuery(@PathVariable UUID queryId);

  @GetMapping(path = "/{queryId}")
  QueryDetails getQuery(@PathVariable UUID queryId,
                        @RequestParam Boolean includeResults,
                        @RequestParam Integer offset,
                        @RequestParam Integer limit);

  @PostMapping
  QueryIdentifier runFqlQueryAsync(@RequestBody SubmitQuery submitQuery);

  @PostMapping(path = "/contents")
    List<Map<String, Object>> getContents(ContentsRequest contentsRequest);

  @GetMapping(path = "/{queryId}/sortedIds")
  List<List<String>> getSortedIds(@PathVariable UUID queryId,
                          @RequestParam Integer offset,
                          @RequestParam Integer limit);
}
