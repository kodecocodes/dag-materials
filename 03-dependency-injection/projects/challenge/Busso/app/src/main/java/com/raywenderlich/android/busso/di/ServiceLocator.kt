package com.raywenderlich.android.busso.di

interface ServiceLocator {
  /**
   * Returns the object of type A bound to a specific name
   */
  fun <A : Any> lookUp(name: String): A
}