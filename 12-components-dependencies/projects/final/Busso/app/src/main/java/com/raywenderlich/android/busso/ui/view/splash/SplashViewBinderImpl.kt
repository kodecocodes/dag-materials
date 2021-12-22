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

package com.raywenderlich.android.busso.ui.view.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import androidx.core.app.ActivityCompat
import com.raywenderlich.android.busso.ui.view.main.MainActivity
import com.raywenderlich.android.ui.navigation.ActivityIntentDestination
import com.raywenderlich.android.ui.navigation.Navigator
import javax.inject.Inject

/** The ViewBinder implementation for the SplashActivity */
class SplashViewBinderImpl @Inject constructor(
    private val navigator: Navigator
) : SplashViewBinder {

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_ID = 1
  }

  private val handler = Handler()
  private lateinit var splashActivity: SplashActivity

  override fun init(rootView: SplashActivity) {
    splashActivity = rootView
  }

  override fun goToMain() {
    handler.post {
      navigator.navigateTo(
          ActivityIntentDestination(
              Intent(splashActivity, MainActivity::class.java)
          )
      )
      with(splashActivity) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
      }
    }
  }

  override fun handleError(error: Throwable) {
    TODO("Not yet implemented")
  }

  override fun requestLocationPermission() {
    ActivityCompat.requestPermissions(
        splashActivity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_ID
    )
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
}