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
package com.raywenderlich.android.location.rx.robot

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.raywenderlich.android.location.FlowLocationConfiguration
import com.raywenderlich.android.location.api.model.LocationEvent
import com.raywenderlich.android.location.api.model.LocationPermissionGranted
import com.raywenderlich.android.location.api.model.LocationPermissionRequest
import com.raywenderlich.android.location.api.permissions.GeoLocationPermissionChecker
import com.raywenderlich.android.location.fakes.FakeGeoLocationPermissionCheckerDisable
import com.raywenderlich.android.location.fakes.FakeGeoLocationPermissionCheckerEnable
import com.raywenderlich.android.location.rx.createFusedLocationFlow
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
private val coroutineDispatcher = TestCoroutineDispatcher()

@ExperimentalCoroutinesApi
class FusedLocationTestEnv(val context: Context) : CoroutineScope {

  private val MY_PROVIDER = "myProvider"

  override val coroutineContext: CoroutineContext
    get() = coroutineDispatcher

  fun Location.copy(time: Long) = Location(this.provider).apply {
    this.latitude = latitude
    this.longitude = longitude
    this.time = time
  }

  private val LOCATION_1 = Location(provider()).apply {
    latitude = 51.509865
    longitude = -0.118092
  }

  private val LOCATION_2 = Location(provider()).apply {
    latitude = 41.9028
    longitude = 12.4964
  }

  lateinit var permissionChecker: GeoLocationPermissionChecker
  val locationEventBag = mutableListOf<LocationEvent>()
  val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context).apply {
    setMockMode(true)
  }

  @ExperimentalCoroutinesApi
  fun initFusedLocationFlow() {
    locationFlow = createFusedLocationFlow(
        fusedLocationProviderClient,
        permissionChecker,
        provider(),
        object : FlowLocationConfiguration {
          override val interval: Long
            get() = 0L
          override val fastestInterval: Long
            get() = 0L
        }
    )
  }

  lateinit var locationFlow: Flow<LocationEvent>

  inner class Given {
    fun permissionIsGranted(): Given {
      permissionChecker = FakeGeoLocationPermissionCheckerEnable
      return this
    }

    fun permissionIsDenied(): Given {
      permissionChecker = FakeGeoLocationPermissionCheckerDisable
      return this
    }
  }

  inner class When {

    fun mockLocation(location: Location): When {
      fusedLocationProviderClient.setMockLocation(location)
      return this
    }
  }

  inner class Then {

    fun permissionRequestIsFiredAt(pos: Int = 0) = assertTrue(
        pos < locationEventBag.size &&
            locationEventBag[pos] is LocationPermissionRequest &&
            locationEventBag[pos].provider == provider()
    )

    fun permissionGrantedIsFiredAt(pos: Int = 0) = assertTrue(
        pos < locationEventBag.size &&
            locationEventBag[pos] is LocationPermissionGranted &&
            locationEventBag[pos].provider == provider()
    )
  }

  lateinit var givenJob: Job

  @ExperimentalCoroutinesApi
  fun Given(fn: Given.() -> Unit) {
    val givenContext = Given()
    givenContext.apply(fn)
    initFusedLocationFlow()

    givenJob = launch {
      locationFlow.collect {
        locationEventBag.add(it)
      }
    }
  }


  fun When(fn: When.() -> Unit) {
    val whenContext = When()
    whenContext.apply(fn)
  }


  fun Then(fn: Then.() -> Unit) {
    val thenContext = Then()
    thenContext.apply(fn)
    givenJob.cancel()
  }

  fun provider(): String = MY_PROVIDER

  fun location1(newTime: Long = 0): Location = LOCATION_1.apply {
    time = newTime
  }

  fun location2(newTime: Long = 0): Location = LOCATION_2.apply {
    time = newTime
  }
}

@ExperimentalCoroutinesApi
fun flowFusedLocationTest(context: Context, fn: FusedLocationTestEnv.() -> Unit) =
    coroutineDispatcher.runBlockingTest {
      val env = FusedLocationTestEnv(context)
      env.apply(fn)
    }