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
package com.raywenderlich.android.busso

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.raywenderlich.android.location.api.model.LocationEvent
import com.raywenderlich.android.location.api.model.LocationPermissionGranted
import com.raywenderlich.android.location.api.model.LocationPermissionRequest
import com.raywenderlich.android.location.api.permissions.GeoLocationPermissionChecker
import com.raywenderlich.android.location.rx.provideRxLocationObservable
import com.raywenderlich.android.ui.navigation.ActivityIntentDestination
import com.raywenderlich.android.ui.navigation.Navigator
import com.raywenderlich.android.ui.navigation.NavigatorImpl
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

/**
 * Splash Screen with the app icon and name at the center, this is also the launch screen and
 * opens up in fullscreen mode. Once launched it waits for 2 seconds after which it opens the
 * MainActivity
 */
class SplashActivity : AppCompatActivity() {

  companion object {
    private const val DELAY_MILLIS = 1000L
    private const val LOCATION_PERMISSION_REQUEST_ID = 1
  }

  private val handler = Handler()
  private val disposables = CompositeDisposable()
  private lateinit var locationManager: LocationManager
  private lateinit var locationObservable: Observable<LocationEvent>
  private lateinit var navigator: Navigator

  private val permissionChecker = object : GeoLocationPermissionChecker {
    override val isPermissionGiven: Boolean
      get() = ContextCompat.checkSelfPermission(
          this@SplashActivity,
          Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    makeFullScreen()
    setContentView(R.layout.activity_splash)
    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    locationObservable = provideRxLocationObservable(locationManager, permissionChecker)
    navigator = NavigatorImpl(this)
  }

  override fun onStart() {
    super.onStart()
    disposables.add(
        locationObservable
            .delay(DELAY_MILLIS, TimeUnit.MILLISECONDS)
            .filter(::isPermissionEvent)
            .subscribe(::handlePermissionRequest, ::handleError)
    )
  }

  private fun makeFullScreen() {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
    supportActionBar?.hide()
  }

  override fun onStop() {
    disposables.clear()
    super.onStop()
  }

  private fun handleError(error: Throwable) {
    TODO("Handle Errors")
  }

  private fun handlePermissionRequest(permissionRequestEvent: LocationEvent) {
    when (permissionRequestEvent) {
      is LocationPermissionRequest -> requestLocationPermission()
      is LocationPermissionGranted -> goToMain()
      else -> throw IllegalStateException("You should never receive this!")
    }
  }

  private fun goToMain() =
      handler.post {
        navigator.navigateTo(
            ActivityIntentDestination(
                Intent(this, MainActivity::class.java)
            )
        )
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
      }

  private fun requestLocationPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) {
      ActivityCompat.requestPermissions(
          this,
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_PERMISSION_REQUEST_ID
      )
      // Show an explanation to the user *asynchronously* -- don't block
      // this thread waiting for the user's response! After the user
      // sees the explanation, try again to request the permission.
    } else {
      // No explanation needed, we can request the permission.
      ActivityCompat.requestPermissions(
          this,
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_PERMISSION_REQUEST_ID
      )
    }
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<String>,
      grantResults: IntArray
  ) {
    when (requestCode) {
      LOCATION_PERMISSION_REQUEST_ID -> {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          // Permission granted! We go on!
          goToMain()
        } else {
          // Request denied, we request again
          requestLocationPermission()
        }
      }
    }
  }

  private fun isPermissionEvent(locationEvent: LocationEvent) =
      locationEvent is LocationPermissionRequest || locationEvent is LocationPermissionGranted

}