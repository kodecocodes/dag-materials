package com.ray

import com.raywenderlich.busso.server.logging.Logger

/** Simple Logger implementation */
class StdLoggerImpl : Logger {
  override fun log(msg: String) {
    println(msg)
  }
}