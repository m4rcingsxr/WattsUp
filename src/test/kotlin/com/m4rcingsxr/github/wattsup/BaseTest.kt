package com.m4rcingsxr.github.wattsup

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.json.jackson.DatabindCodec
import org.junit.jupiter.api.BeforeAll

internal abstract class BaseTest {
  companion object {
    @JvmStatic
    @BeforeAll
    fun setup() {
      DatabindCodec.mapper().registerKotlinModule()
    }
  }
}
