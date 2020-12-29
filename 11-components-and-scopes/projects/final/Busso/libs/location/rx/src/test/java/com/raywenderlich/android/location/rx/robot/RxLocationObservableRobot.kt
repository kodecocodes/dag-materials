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
import android.location.LocationManager
import com.nhaarman.mockitokotlin2.mock
import com.raywenderlich.android.location.api.model.*
import com.raywenderlich.android.location.api.permissions.GeoLocationPermissionChecker
import com.raywenderlich.android.location.rx.provideRxLocationObservable
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Assert.*
import org.mockito.Mockito
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowLocationManager
import kotlin.math.abs

class LocationTestEnv(context: Context) {

  private val MY_PROVIDER = "myProvider"

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

  val permissionChecker: GeoLocationPermissionChecker = mock()
  val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
  val testObserver = TestObserver<LocationEvent>()
  val shadowLocationManager: ShadowLocationManager = shadowOf(locationManager)

  lateinit var rxObservable: Observable<LocationEvent>

  inner class Given {
    fun permissionIsGranted(): Given {
      Mockito.`when`(permissionChecker.isPermissionGiven).thenReturn(true)
      return this
    }

    fun permissionIsDenied(): Given {
      Mockito.`when`(permissionChecker.isPermissionGiven).thenReturn(false)
      return this
    }

    fun lastKnownLocationIs(location: Location): Given {
      shadowLocationManager.setLastKnownLocation(provider(), location)
      return this
    }

    fun noLastKnownLocationAvailable(): Given {
      shadowLocationManager.setLastKnownLocation(provider(), null)
      return this
    }
  }

  inner class When {
    fun subscribeRx(): When {
      rxObservable =
          provideRxLocationObservable(locationManager, permissionChecker, MY_PROVIDER, 0, 0F)
      rxObservable
          .subscribe(testObserver)
      return this
    }

    fun locationChangedTo(newLocation: Location): When {
      shadowLocationManager.simulateLocation(newLocation)
      return this
    }

    fun locationNotAvailable(): When {
      shadowLocationManager.simulateLocation(null)
      return this
    }

    fun enableProvider(): When {
      shadowLocationManager.setProviderEnabled(provider(), true)
      return this
    }

    fun disableProvider(): When {
      shadowLocationManager.setProviderEnabled(provider(), false)
      return this
    }
  }

  inner class Then {
    fun permissionRequestIsFired() =
        assertNotNull(testObserver.values().find {
          it is LocationPermissionRequest && it.provider == MY_PROVIDER
        })

    fun permissionGrantedIsFired() =
        assertNotNull(testObserver.values().find {
          it is LocationPermissionGranted && it.provider == MY_PROVIDER
        })

    fun noPermissionRequestIsFired() {
      testObserver.values().forEach {
        assertFalse(it is LocationPermissionRequest)
      }
    }

    /**
     * Checks if the provided location is present in the result
     */
    fun containsLocation(latitude: Double, longitude: Double) {
      val found = testObserver.values()
          .filter { it is LocationData }
          .map {
            (it as LocationData)
          }.map {
            it.location
          }.filter {
            abs(it.latitude - latitude) < 0.001 &&
                abs(it.longitude - longitude) < 0.001
          }.firstOrNull()
      assertTrue(found != null)
    }

    fun receivedLocationNotAvailable() =
        assertNotNull(testObserver.values().find {
          it is LocationNotAvailable && it.provider == MY_PROVIDER
        })

    fun providerEnabledReceived() {
      val found = testObserver.values()
          .filter { it is LocationProviderEnabledChanged }
          .map {
            (it as LocationProviderEnabledChanged)
          }.filter {
            it.enabled
          }.firstOrNull()
      assertTrue(found != null)
    }

    fun providerDisabledReceived() {
      val found = testObserver.values()
          .filter { it is LocationProviderEnabledChanged }
          .map {
            (it as LocationProviderEnabledChanged)
          }.filter {
            !it.enabled
          }.firstOrNull()
      assertTrue(found != null)
    }

    fun isComplete() {
      testObserver.assertComplete()
    }
  }

  fun Given(fn: Given.() -> Unit) {
    val givenContext = Given()
    givenContext.apply(fn)
  }

  fun When(fn: When.() -> Unit) {
    val whenContext = When()
    whenContext.apply(fn)
  }

  fun Then(fn: Then.() -> Unit) {
    val thenContext = Then()
    thenContext.apply(fn)
  }

  fun provider(): String = MY_PROVIDER

  fun location1(time: Long = 0): Location = LOCATION_1.copy(time)

  fun location2(time: Long = 0): Location = LOCATION_2.copy(time)
}


fun rxLocationTest(context: Context, fn: LocationTestEnv.() -> Unit) {
  val env = LocationTestEnv(context)
  env.apply(fn)
}
