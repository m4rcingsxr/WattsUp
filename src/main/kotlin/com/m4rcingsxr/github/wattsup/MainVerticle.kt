package com.m4rcingsxr.github.wattsup

import io.vertx.core.VerticleBase
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class MainVerticle : VerticleBase() {

  companion object {
    private val LOG = LoggerFactory.getLogger(MainVerticle::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
      val vertx = Vertx.vertx()
      vertx.deployVerticle(MainVerticle())
    }
  }
}
