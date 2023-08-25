package org.folio.fqm.edge.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dummy controller to override the default tenant controller from folio-spring-base,
 * since the default isn't necessary for this edge module.
 */
@Log4j2
@RestController("folioTenantController")
@RequestMapping(value = "/_/")
public class EdgeFqmTenantController {}
