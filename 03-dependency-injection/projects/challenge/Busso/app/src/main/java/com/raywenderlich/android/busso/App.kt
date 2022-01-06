package com.raywenderlich.android.busso

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.android.busso.di.ServiceLocator
import com.raywenderlich.android.busso.di.ServiceLocatorImpl

class App : Application() {
  lateinit var serviceLocator: ServiceLocator

  override fun onCreate() {
    super.onCreate()
    serviceLocator = ServiceLocatorImpl(this)
  }
}

internal fun <A : Any> AppCompatActivity.lookUp(name: String): A =
  (applicationContext as App).serviceLocator.lookUp(name)