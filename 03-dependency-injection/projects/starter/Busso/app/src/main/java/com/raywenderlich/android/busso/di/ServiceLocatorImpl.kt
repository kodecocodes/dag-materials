package com.raywenderlich.android.busso.di

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.raywenderlich.android.busso.permission.GeoLocationPermissionCheckerImpl
import com.raywenderlich.android.location.rx.provideRxLocationObservable
import java.lang.IllegalArgumentException

const val LOCATION_OBSERVABLE = "LocationObservable"
const val GEO_PERMISSION_CHECKER = "GeoPermissionChecker"

class ServiceLocatorImpl(private val context: Context) : ServiceLocator {

    private val locationManager: LocationManager
        = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val geoLocationPermissionChecker = GeoLocationPermissionCheckerImpl(context)

    private val locationObservable
        = provideRxLocationObservable(locationManager, geoLocationPermissionChecker)

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ServiceCast")
    override fun <T : Any> lookUp(name: String): T {
        return when(name) {
            LOCATION_OBSERVABLE -> locationObservable as T
            else -> throw IllegalArgumentException("No component found for '$name'")
        }
    }
}
