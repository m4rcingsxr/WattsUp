package com.m4rcingsxr.github.wattsup.config.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class Configuration(
  val server: ServerConfiguration,
  val datasource: DatasourceConfiguration
)
