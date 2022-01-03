package com.raywenderlich.android.busso.di

interface ServiceLocator {
    fun <T : Any> lookUp(name: String): T
}