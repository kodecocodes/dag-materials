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

package com.raywenderlich.android.plugins.engine.ui

import android.view.View
import com.raywenderlich.android.di.scopes.FragmentScope
import com.raywenderlich.android.location.api.model.LocationData
import com.raywenderlich.android.location.api.model.LocationEvent
import com.raywenderlich.android.location.api.model.LocationPermissionGranted
import com.raywenderlich.android.location.api.model.LocationPermissionRequest
import com.raywenderlich.android.mvp.impl.BasePresenter
import com.raywenderlich.android.plugins.api.InformationPluginRegistry
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/** The InformationPluginPresenter implementation */
@FragmentScope
class InformationPluginPresenterImpl @Inject constructor(
  private val informationPluginRegistry: InformationPluginRegistry,
  private val locationObservable: Observable<LocationEvent>
) : BasePresenter<View, InformationPluginViewBinder>(),
  InformationPluginPresenter {

  private val disposables = CompositeDisposable()

  override fun start() {
    disposables.add(
      locationObservable.filter(::isLocationEvent)
        .map { locationEvent ->
          locationEvent as LocationData
        }
        .firstElement()
        .map { locationData ->
          val res = informationPluginRegistry.plugins().map { endpoint ->
            val location = locationData.location
            endpoint.fetchInformation(location.latitude, location.longitude)
              .toFlowable()
          }
          Flowable
            .merge(res)
            .collectInto(mutableListOf<String>()) { acc, item ->
              acc.add(item.message)
            }
        }
        .subscribe(::manageResult, ::handleError)
    )
  }

  fun manageResult(single: Single<MutableList<String>>) {
    useViewBinder {
      single
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ messages ->
          displayInformation(messages)
        }, ::handleError)
    }
  }

  private fun handleError(throwable: Throwable) {
    useViewBinder {
      displayInformation(listOf("Error: $throwable"))
    }
  }

  override fun stop() {
    disposables.clear()
  }

  private fun isLocationEvent(locationEvent: LocationEvent) =
    locationEvent !is LocationPermissionRequest &&
        locationEvent !is LocationPermissionGranted
}