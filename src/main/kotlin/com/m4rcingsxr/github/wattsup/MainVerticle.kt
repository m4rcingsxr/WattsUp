package com.m4rcingsxr.github.wattsup

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.m4rcingsxr.github.wattsup.config.ConfigurationLoader
import io.vertx.core.Future
import io.vertx.core.VerticleBase
import io.vertx.core.Vertx
import io.vertx.core.json.jackson.DatabindCodec
import org.slf4j.LoggerFactory

internal class MainVerticle : VerticleBase() {

  companion object {
    private val LOG = LoggerFactory.getLogger(MainVerticle::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
      DatabindCodec.mapper().registerKotlinModule()

      Vertx.vertx()
        .exceptionHandler { err -> LOG.error("Unexpected exception occurred", err) }
        .deployVerticle(MainVerticle())
        .onSuccess { id -> LOG.info("Deployed {} with id {}", MainVerticle::class.java.getSimpleName(), id) }
        .onFailure { err -> LOG.error("Failed to deploy MainVerticle", err) }
    }
  }

  override fun start(): Future<*> {
    return ConfigurationLoader().load(vertx = vertx)
      .compose { config ->
        vertx.createHttpServer()
          .requestHandler { req -> req.response().end("Hello World!!") }
          .listen(config.server.port)
          .onSuccess { server -> LOG.info("HTTP server successfully started on port ${server.actualPort()}") }
          .onFailure { err -> LOG.error("Failed to start HTTP server during verticle deployment", err) }
      }
  }
}
