package org.folio.fqm.edge.client;

import org.folio.querytool.domain.dto.ColumnValues;
import org.folio.querytool.domain.dto.EntityType;
import org.folio.fqm.edge.domain.dto.EntityTypeSummaries;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.UUID;

@HttpExchange(url = "entity-types")
public interface EntityTypesClient {

  @GetExchange
  EntityTypeSummaries getEntityTypeSummary(@RequestParam(required = false) List<UUID> ids, @RequestParam(required = false) Boolean includeInaccessible, @RequestParam(required = false) Boolean includeAll);

  @GetExchange(url = "/{entityTypeId}/columns/{columnName}/values")
  ColumnValues getColumnValues(@PathVariable UUID entityTypeId,
                               @PathVariable String columnName,
                               @RequestParam(required = false) String search);

  @GetExchange(url = "/{entityTypeId}/field-values")
  ColumnValues getFieldValues(@PathVariable UUID entityTypeId,
                               @RequestParam("field") String fieldName,
                               @RequestParam(value = "search", required = false) String search);

  @GetExchange(url = "/{entityTypeId}")
  EntityType getEntityType(@PathVariable UUID entityTypeId, @RequestParam(required = false) Boolean includeHidden);

}
