package org.folio.fqm.edge.client;

import org.folio.fqm.edge.client.config.OkapiFeignClientConfig;
import org.folio.querytool.domain.dto.ColumnValues;
import org.folio.querytool.domain.dto.EntityType;
import org.springframework.cloud.openfeign.FeignClient;
import org.folio.fqm.edge.domain.dto.EntityTypeSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "entity-types", configuration = OkapiFeignClientConfig.class)
public interface EntityTypesClient {

  @GetMapping
  EntityTypeSummaryResponse getEntityTypeSummary(@RequestParam List<UUID> ids);

  @GetMapping(path = "/{entityTypeId}/columns/{columnName}/values")
  ColumnValues getColumnValues(@PathVariable UUID entityTypeId,
                               @PathVariable String columnName,
                               @RequestParam String search);

  @GetMapping(path = "/{entityTypeId}")
  EntityType getEntityType(@PathVariable UUID entityTypeId);

  // not yet mirrored by edge-fqm, so we will temporarily add a record.
  // will be properly added to openapi spec in EDGFQM-26
  record EntityTypeSummaryResponse(List<EntityTypeSummary> entityTypes, String _version) {}
}
