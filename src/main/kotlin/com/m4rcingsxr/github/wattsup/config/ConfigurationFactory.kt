package com.m4rcingsxr.github.wattsup.config

import com.m4rcingsxr.github.wattsup.config.model.Configuration
import io.vertx.core.json.JsonObject

internal object ConfigurationFactory {
  private const val SERVER_PORT = "server.port"

  private const val DATASOURCE_REACTIVE_URL = "datasource.reactiveUrl"
  private const val DATASOURCE_JDBC_URL = "datasource.jdbcUrl"
  private const val DATASOURCE_USERNAME = "datasource.username"
  private const val DATASOURCE_PASSWORD = "datasource.password"

  private val serverProperties = listOf(
    SERVER_PORT
  )

  private val datasourceProperties = listOf(
    DATASOURCE_REACTIVE_URL,
    DATASOURCE_JDBC_URL,
    DATASOURCE_USERNAME,
    DATASOURCE_PASSWORD
  )

  fun build(config: JsonObject): Configuration {
    validateServerConfiguration(config = config)
    validateDatasourceConfiguration(config = config)
    return config.mapTo(Configuration::class.java)
  }

  private fun validateServerConfiguration(config: JsonObject) {
    serverProperties.forEach { property -> config.requireProperty(property = property) }
  }

  private fun validateDatasourceConfiguration(config: JsonObject) {
    datasourceProperties.forEach { property -> config.requireProperty(property = property) }
  }

  private fun JsonObject.requireProperty(property: String) {
    check(this.hasPath(path = property)) { "Required configuration property '$property' is missing" }
  }

  private fun JsonObject.hasPath(path: String): Boolean {
    val parts = path.split(".")
    var current: Any? = this

    for (part in parts) {
      if (current !is JsonObject || current.containsKey(part).not()) {
        return false
      }
      current = current.getValue(part)
    }
    return true
  }
}
