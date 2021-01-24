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
package com.raywenderlich.android.location.api.model

/**
 * This is the class which defines all the possible type of information you
 * can get from a LocationManager
 */
sealed class LocationEvent(val provider: String? = null)

/**
 * The case when we need to ask for permission in order to get data from here.
 */
class LocationPermissionRequest(provider: String?) : LocationEvent(provider)

/**
 * The case when we need to notify that the permission has been given. This can be
 * used to navigate to a different screen or to make some other decision
 */
class LocationPermissionGranted(provider: String?) : LocationEvent(provider)

/**
 * The location is not available because the provider needs some time. The location info
 * will be provided eventually.
 */
class LocationNotAvailable(provider: String?) : LocationEvent(provider)

/**
 * This encapsulate the location information. Location cannot be null otherwise the event would be
 * a LocationNotAvailable
 */
class LocationData(provider: String?, val location: GeoLocation) : LocationEvent(provider)

/**
 * This is emitted when we want to emit information about a change in status
 */
class LocationStatus(provider: String?, status: Int, val extras: Map<String, Any>) :
  LocationEvent(provider)

/**
 * This is emitted when the provided changes its state between enabled and disabled which is the
 * meaning of the second parameter
 */
class LocationProviderEnabledChanged(provider: String?, val enabled: Boolean) :
  LocationEvent(provider)
