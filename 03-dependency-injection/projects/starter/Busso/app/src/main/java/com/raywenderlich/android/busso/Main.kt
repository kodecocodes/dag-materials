package com.raywenderlich.android.busso

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.android.busso.di.ServiceLocator
import com.raywenderlich.android.busso.di.ServiceLocatorImpl

class Main : Application() {

    lateinit var serviceLocator: ServiceLocator

    override fun onCreate() {
        super.onCreate()

        serviceLocator = ServiceLocatorImpl(this)
    }
}

internal fun <T : Any> AppCompatActivity.lookUp(name: String): T {
    return (this.application as Main).serviceLocator.lookUp(name)
}
