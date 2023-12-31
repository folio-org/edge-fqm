# edge-fqm

Copyright (C) 2023 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

An edge API for FOLIO Query Machine.

# Introduction
Provides an ability to retrieve query and entity type information from FOLIO

# Overview
The purpose of this edge API is to bridge the gap between 3rd party discovery services and FOLIO mod-fqm-manager module.

# Security
The edge-fqm API is secured via the facilities provided by edge-common. More specifically, via API Key. See edge-common for additional details.See [edge-common-spring](https://github.com/folio-org/edge-common-spring)

# Installation/Deployment

## Configuration

* See [edge-common](https://github.com/folio-org/edge-common) for a description of how configuration works.

***System properties***

| Property             | Default                                   | Description                                                         |
|----------------------|-------------------------------------------|---------------------------------------------------------------------|
| `server.port`        | `8081`                                    | Server port to listen on                                            |
| `okapi_url`          | `http://okapi:9130`	                      | Okapi (URL)                                                         |
| `secure_store`       | `Ephemeral`                               | Type of secure store to use.  Valid: `Ephemeral`, `AwsSsm`, `Vault` |
| `secure_store_props` | `src/main/resources/ephemeral.properties` | Path to a properties file specifying secure store configuration     |


### Configuring spring-boot

Spring boot properties can be overridden using the specified environment variables, if it is not it can be done using
one of the following approaches (see also the
documentation [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/1.5.6.RELEASE/reference/html/boot-features-external-config.html)):

1. Using the environment variable `SPRING_APPLICATION_JSON` (example: `SPRING_APPLICATION_JSON='{"foo":{"bar":"spam"}}'`)
2. Using the system variables within the `JAVA_OPTIONS` (example: `JAVA_OPTIONS=-Xmx400m -Dserver.port=1234`)
