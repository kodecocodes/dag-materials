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

package com.raywenderlich.android.raytracker.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.raywenderlich.android.busso.permission.GeoLocationPermissionCheckerImpl
import com.raywenderlich.android.location.api.model.LocationEvent
import com.raywenderlich.android.location.api.model.LocationPermissionGranted
import com.raywenderlich.android.location.api.model.LocationPermissionRequest
import com.raywenderlich.android.location.rx.createFusedLocationFlow
import com.raywenderlich.android.raytracker.conf.RayTrackFlowLocationConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/** The ViewModel for the SplashActivity */
@ExperimentalCoroutinesApi
class SplashViewModel(
    application: Application
) : AndroidViewModel(application) {

  private val _locationEvents = MutableLiveData<LocationEvent>()

  private val fusedLocationProviderClient: FusedLocationProviderClient =
      LocationServices.getFusedLocationProviderClient(application)

  private val locationFlow = createFusedLocationFlow(
      fusedLocationProviderClient,
      GeoLocationPermissionCheckerImpl(application),
      config = RayTrackFlowLocationConfig
  )

  fun start() {
    viewModelScope.launch {
      locationFlow
          .filter {
            isPermissionEvent(it)
          }
          .collect {
            _locationEvents.value = it
          }
    }
  }

  fun locations(): LiveData<LocationEvent> = _locationEvents

  private fun isPermissionEvent(locationEvent: LocationEvent) =
      locationEvent is LocationPermissionRequest || locationEvent is LocationPermissionGranted
}