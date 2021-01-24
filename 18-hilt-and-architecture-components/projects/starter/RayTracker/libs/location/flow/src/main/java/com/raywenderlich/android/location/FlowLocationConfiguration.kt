package com.raywenderlich.android.location

/** Configuration for the FlowLocation implementation */
interface FlowLocationConfiguration {
  val interval: Long
  val fastestInterval: Long
}