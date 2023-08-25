package org.folio.fqm.edge.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.folio.fqm.edge.client.QueryClient;
import org.folio.querytool.domain.dto.QueryDetails;
import org.folio.querytool.domain.dto.QueryIdentifier;
import org.folio.querytool.domain.dto.ResultsetPage;
import org.folio.querytool.domain.dto.SubmitQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QueryService {

  private final QueryClient queryClient;

  public ResultsetPage runFqlQuery(@NotNull String query, @NotNull UUID entityTypeId, List<String> fields, UUID afterId, Integer limit) {
    return queryClient.runFqlQuery(query, entityTypeId, fields, afterId, limit);
  }

  public void deleteQuery(UUID queryId) {
    queryClient.deleteQuery(queryId);
  }

  public QueryDetails getQuery(UUID queryId, Boolean includeResults, Integer offset, Integer limit) {
    return queryClient.getQuery(queryId, includeResults, offset, limit);
  }

  public QueryIdentifier runFqlQueryAsync(SubmitQuery submitQuery) {
    return queryClient.runFqlQueryAsync(submitQuery);
  }
}
