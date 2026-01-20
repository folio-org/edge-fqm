package org.folio.fqm.edge.controller;

import lombok.RequiredArgsConstructor;
import org.folio.fqm.edge.domain.dto.EntityTypeSummaries;
import org.folio.fqm.edge.service.EntityTypesService;
import org.folio.querytool.domain.dto.ColumnValues;
import org.folio.querytool.domain.dto.EntityType;
import org.folio.querytool.rest.resource.EntityTypesApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/", "/fqm"})
public class EntityTypesController implements org.folio.fqm.edge.rest.resource.EntityTypesApi, org.folio.querytool.rest.resource.EntityTypesApi {

  private final EntityTypesService entityTypesService;

  @Override
  public ResponseEntity<EntityTypeSummaries> getEntityTypeSummary(List<UUID> ids, Boolean includeInaccessible, Boolean includeAll) {
    return ResponseEntity.ok(entityTypesService.getEntityTypeSummary(ids, includeInaccessible, includeAll));
  }

  @Override
  @Deprecated
  public ResponseEntity<ColumnValues> getColumnValues(UUID entityTypeId, String columnName, String search) {
    return ResponseEntity.ok(entityTypesService.getColumnValues(entityTypeId, columnName, search));
  }

  @Override
  public ResponseEntity<ColumnValues> getFieldValues(UUID entityTypeId, String columnName, String search) {
    return ResponseEntity.ok(entityTypesService.getFieldValues(entityTypeId, columnName, search));
  }

  @Override
  public ResponseEntity<EntityType> getEntityType(UUID entityTypeId, Boolean includeHidden) {
    return ResponseEntity.ok(entityTypesService.getEntityType(entityTypeId, includeHidden));
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return EntityTypesApi.super.getRequest();
  }
}
