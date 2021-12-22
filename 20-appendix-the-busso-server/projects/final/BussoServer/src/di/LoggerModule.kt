package com.raywenderlich.busso.server.di

import com.ray.StdLoggerImpl
import com.raywenderlich.busso.server.logging.Logger
import org.koin.dsl.module

/** The Module for the Logger implementation */
val loggerModule = module {
  factory<Logger> { StdLoggerImpl() }
}