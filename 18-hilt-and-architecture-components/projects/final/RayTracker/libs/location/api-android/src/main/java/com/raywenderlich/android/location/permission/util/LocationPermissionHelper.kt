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

package com.raywenderlich.android.location.permission.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.raywenderlich.android.location.api.model.LocationEvent
import com.raywenderlich.android.location.api.model.LocationPermissionGranted
import com.raywenderlich.android.location.api.model.LocationPermissionRequest

/** Helper for managing location permissions requests */
class LocationPermissionHelper(
    private val activity: AppCompatActivity
) {

  var onLocationGranted: (() -> Unit)? = null

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_ID = 1
  }


  fun handlePermissionEvent(permissionEvent: LocationEvent) {
    when (permissionEvent) {
      is LocationPermissionRequest -> requestLocationPermission()
      is LocationPermissionGranted -> onLocationGranted?.invoke()
      else -> throw IllegalStateException("You should never receive this!")
    }
  }

  fun handlePermissionsResult(
      requestCode: Int,
      grantResults: IntArray
  ) {
    when (requestCode) {
      LOCATION_PERMISSION_REQUEST_ID -> {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          // Permission granted! We go on!
          onLocationGranted?.invoke()
        } else {
          // Request denied, we request again
          requestLocationPermission()
        }
      }
    }
  }

  private fun requestLocationPermission() {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_ID
    )
  }
}