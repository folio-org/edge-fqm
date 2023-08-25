package org.folio.fqm.edge.client;

import jakarta.validation.constraints.NotNull;
import org.folio.fqm.edge.client.config.OkapiFeignClientConfig;
import org.folio.querytool.domain.dto.QueryDetails;
import org.folio.querytool.domain.dto.QueryIdentifier;
import org.folio.querytool.domain.dto.ResultsetPage;
import org.folio.querytool.domain.dto.SubmitQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "query", configuration = OkapiFeignClientConfig.class)
public interface QueryClient {

  @GetMapping
  ResultsetPage runFqlQuery(@RequestParam @NotNull String query,
                            @RequestParam @NotNull UUID entityTypeId,
                            @RequestParam List<String> fields,
                            @RequestParam UUID afterId,
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

}
