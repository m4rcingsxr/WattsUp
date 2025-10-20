package com.m4rcingsxr.github.wattsup.config

import com.m4rcingsxr.github.wattsup.BaseTest
import io.vertx.core.Vertx
import io.vertx.core.file.FileSystemException
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class ConfigurationLoaderTest : BaseTest() {

  @Test
  fun `should successfully load configuration from yaml resource with not handled properties`(vertx: Vertx, context: VertxTestContext) {
    ConfigurationLoader()
      .withConfigurationFile(configurationFile = "application.yml")
      .load(vertx = vertx)
      .onSuccess { config ->
        context.verify {
          assertNotNull(config)
          assertEquals(4200, config.server.port)
          assertEquals("jdbc:postgresql://localhost:5432/test", config.datasource.jdbcUrl)
          assertEquals("postgresql://localhost:5432/test", config.datasource.reactiveUrl)
          assertEquals("user", config.datasource.username)
          assertEquals("pass", config.datasource.password)
        }
          .completeNow()
      }
      .onFailure { err -> context.failNow(err) }
  }

  @Test
  fun `should successfully load configuration when overridden by system properties`(
    vertx: Vertx,
    context: VertxTestContext
  ) {
    System.setProperty("server.port", "9999")
    try {
      ConfigurationLoader()
        .withConfigurationFile(configurationFile = "application.yml")
        .load(vertx = vertx)
        .onSuccess { config ->
          context.verify {
            assertNotNull(config)
            assertEquals(9999, config.server.port)
            assertEquals("jdbc:postgresql://localhost:5432/test", config.datasource.jdbcUrl)
            assertEquals("postgresql://localhost:5432/test", config.datasource.reactiveUrl)
            assertEquals("user", config.datasource.username)
            assertEquals("pass", config.datasource.password)
          }
            .completeNow()
        }
        .onFailure { err -> context.failNow(err) }
    } finally {
      System.clearProperty("server.port")
    }
  }

  @Test
  fun `should throw IllegalStateException if configuration is missing required property`(
    vertx: Vertx,
    context: VertxTestContext
  ) {
    ConfigurationLoader()
      .withConfigurationFile(configurationFile = "missing-property.yml")
      .load(vertx = vertx)
      .onSuccess { _ -> context.failNow("Expected configuration loading to fail due to missing required property") }
      .onFailure { err ->
        context.verify {
          assertInstanceOf(IllegalStateException::class.java, err)
          assertNotNull(err.message)
          assertEquals("Required configuration property 'server.port' is missing", err.message)
        }
          .completeNow()
      }
  }

  @Test
  fun `should throw IllegalStateException when yaml file path does not exist`(
    vertx: Vertx,
    context: VertxTestContext
  ) {
    ConfigurationLoader()
      .withConfigurationFile(configurationFile = "non-existing.yml")
      .load(vertx = vertx)
      .onSuccess { _ -> context.failNow("Expected configuration loading to fail due to missing required property") }
      .onFailure { err ->
        context.verify {
          assertInstanceOf(FileSystemException::class.java, err)
          assertNotNull(err.message)
          assertEquals("Unable to read file at path 'non-existing.yml'", err.message)
        }
          .completeNow()
      }
  }
}
