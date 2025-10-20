package com.m4rcingsxr.github.wattsup.config

import com.m4rcingsxr.github.wattsup.config.model.Configuration
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

/**
 * System properties > YAML file defaults.
 */
internal class ConfigurationLoader {

  private var configurationFile = "application.yml"

  private val LOG = LoggerFactory.getLogger(ConfigurationLoader::class.java)

  fun withConfigurationFile(configurationFile: String): ConfigurationLoader {
    this.configurationFile = configurationFile
    return this
  }

  fun load(vertx: Vertx): Future<Configuration> {
    LOG.info("Loading application configuration from provided store options")
    val retriever = ConfigRetriever.create(
      vertx,
      ConfigRetrieverOptions()
        .addStore(buildYamlFileStoreOptions())
        .addStore(buildSystemPropertyStoreOptions())
    )

    return retriever.config
      .onSuccess { json ->
        LOG.info("Merged configuration from provided store options loaded successfully")
        LOG.trace("configuration json={}", json.encodePrettily())
      }
      .map(ConfigurationFactory::build)
      .onSuccess { config -> LOG.info("Configuration validated and mapped successfully config=$config") }
      .onFailure { err -> LOG.error("Unexpected exception occurred. Unable to load configuration", err) }
      .recover { err -> Future.failedFuture(err) }
  }

  private fun buildYamlFileStoreOptions(): ConfigStoreOptions {
    val type = "file"
    val format = "yaml"

    return ConfigStoreOptions()
      .setType(type)
      .setFormat(format)
      .setConfig(JsonObject().put("path", configurationFile))
      .also { LOG.debug("yaml configuration options created path=$configurationFile type=$type format=$format") }
  }

  private fun buildSystemPropertyStoreOptions(): ConfigStoreOptions {
    val cache = false
    val hierarchical = true

    return ConfigStoreOptions()
      .setType("sys")
      .setConfig(JsonObject().put("cache", cache).put("hierarchical", hierarchical))
      .also { LOG.debug("system properties configuration options created cache=$cache hierarchical=$hierarchical") }

  }
}
