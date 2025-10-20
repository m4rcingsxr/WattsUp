package com.m4rcingsxr.github.wattsup.config.model

internal data class DatasourceConfiguration(
  val reactiveUrl: String,
  val jdbcUrl: String,
  val username: String,
  val password: String
)
