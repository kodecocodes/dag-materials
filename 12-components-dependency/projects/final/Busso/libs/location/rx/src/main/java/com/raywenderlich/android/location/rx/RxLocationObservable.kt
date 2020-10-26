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
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.raywenderlich.android.location.api.model.*
import com.raywenderlich.android.location.api.permissions.GeoLocationPermissionChecker
import io.reactivex.Observable

/**
 * We want to create an RxObservable as hot observable for location data. We assume the permission
 * has been already requested because if not this emits the event
 */
@SuppressLint("MissingPermission")
fun provideRxLocationObservable(
    locationManager: LocationManager,
    geoLocationPermissionChecker: GeoLocationPermissionChecker,
    provider: String = LocationManager.GPS_PROVIDER,
    minTime: Long = 1000L,
    minDistance: Float = 100F
) =
    Observable.create<LocationEvent> { emitter ->
      // We check for the permission.
      if (geoLocationPermissionChecker.isPermissionGiven) {
        // We send an event about the permission
        emitter.onNext(LocationPermissionGranted(provider))
        // If last location is available we sent it
        val lastLocation = locationManager.getLastKnownLocation(provider)
        if (lastLocation != null) {
          emitter.onNext(
              LocationData(
                  provider,
                  GeoLocation(lastLocation.latitude, lastLocation.longitude)
              )
          )
        } else {
          emitter.onNext(LocationNotAvailable(provider))
        }
        locationManager.requestLocationUpdates(
            provider,
            minTime,
            minDistance,
            object : LocationListener {
              override fun onLocationChanged(location: Location?) {
                if (location != null) {
                  emitter.onNext(
                      LocationData(
                          provider,
                          GeoLocation(location.latitude, location.longitude)
                      )
                  )
                } else {
                  emitter.onNext(LocationNotAvailable(provider))
                }
              }

              override fun onStatusChanged(
                  provider: String?,
                  status: Int,
                  extras: Bundle?
              ) {
                emitter.onNext(LocationStatus(provider, status, emptyMap()))
              }

              override fun onProviderEnabled(provider: String?) {
                emitter.onNext(LocationProviderEnabledChanged(provider, true))
              }

              override fun onProviderDisabled(provider: String?) {
                emitter.onNext(LocationProviderEnabledChanged(provider, false))
              }

            })
      } else {
        // If the permission is not given we need to generate a request for permission and then complete
        emitter.onNext(LocationPermissionRequest(provider))
        emitter.onComplete()
      }
    }
