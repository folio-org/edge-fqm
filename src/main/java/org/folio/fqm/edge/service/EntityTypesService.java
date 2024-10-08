package org.folio.fqm.edge.service;

import lombok.RequiredArgsConstructor;
import org.folio.fqm.edge.client.EntityTypesClient;
import org.folio.fqm.edge.domain.dto.EntityTypeSummary;
import org.folio.querytool.domain.dto.ColumnValues;
import org.folio.querytool.domain.dto.EntityType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EntityTypesService {

  private final EntityTypesClient entityTypesClient;

  public List<EntityTypeSummary> getEntityTypeSummary(List<UUID> ids) {
    return entityTypesClient.getEntityTypeSummary(ids).entityTypes();
  }

  public ColumnValues getColumnValues(UUID entityTypeId, String columnName, String search) {
    return entityTypesClient.getColumnValues(entityTypeId, columnName, search);
  }

  public EntityType getEntityType(UUID entityTypeId, Boolean includeHidden) {
    return entityTypesClient.getEntityType(entityTypeId, includeHidden);
  }
}
