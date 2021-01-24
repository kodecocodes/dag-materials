/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.android.location.rx

import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.raywenderlich.android.location.FlowLocationConfiguration
import com.raywenderlich.android.location.api.model.LocationPermissionGranted
import com.raywenderlich.android.location.api.model.LocationPermissionRequest
import com.raywenderlich.android.location.api.permissions.GeoLocationPermissionChecker
import com.raywenderlich.android.location.permission.util.toLocationData
import com.raywenderlich.android.location.util.awaitLastLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * We want to create an RxObservable as hot observable for location data. We assume the permission
 * has been already requested because if not this emits the event
 */
@ExperimentalCoroutinesApi
@SuppressLint("MissingPermission")
fun createFusedLocationFlow(
    fusedLocationProviderClient: FusedLocationProviderClient,
    geoLocationPermissionChecker: GeoLocationPermissionChecker,
    provider: String = LocationManager.GPS_PROVIDER,
    config: FlowLocationConfiguration
) = callbackFlow {
  val locationCallback = object : LocationCallback() {
    override fun onLocationResult(result: LocationResult?) {
      result?.let {
        for (location in result.locations) {
          offer(location.toLocationData())
        }
      }
    }
  }
  // We check for the permission.
  if (geoLocationPermissionChecker.isPermissionGiven) {
    // We send an event about the permission
    send(LocationPermissionGranted(provider))

    fusedLocationProviderClient.awaitLastLocation()?.let { lastLocation ->
      send(lastLocation)
    }

    val locationRequest = LocationRequest().apply {
      interval = config.interval
      fastestInterval = config.fastestInterval
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    fusedLocationProviderClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    ).addOnFailureListener { e ->
      close(e) // in case of error, close the Flow
    }
  } else {
    // If the permission is not given we need to generate a request for permission and then complete
    send(LocationPermissionRequest(provider))
  }
  awaitClose {
    fusedLocationProviderClient.removeLocationUpdates(locationCallback) // clean up when Flow collection ends
  }
}