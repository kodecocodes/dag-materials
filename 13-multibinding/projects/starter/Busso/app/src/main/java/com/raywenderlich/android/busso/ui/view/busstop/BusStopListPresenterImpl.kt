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

package com.raywenderlich.android.busso.ui.view.busstop

import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import com.raywenderlich.android.busso.R
import com.raywenderlich.android.busso.di.scopes.FragmentScope
import com.raywenderlich.android.busso.network.BussoEndpoint
import com.raywenderlich.android.busso.ui.view.busarrival.BusArrivalFragment
import com.raywenderlich.android.busso.ui.view.busarrival.BusArrivalFragment.Companion.BUS_STOP_ID
import com.raywenderlich.android.location.api.model.*
import com.raywenderlich.android.ui.mvp.impl.BasePresenter
import com.raywenderlich.android.ui.navigation.FragmentFactoryDestination
import com.raywenderlich.android.ui.navigation.Navigator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@FragmentScope
class BusStopListPresenterImpl @Inject constructor(
    private val navigator: Navigator,
    private val locationObservable: Observable<LocationEvent>,
    private val bussoEndpoint: BussoEndpoint
) : BasePresenter<View, BusStopListViewBinder>(),
    BusStopListPresenter {

  init {
    Log.d("BUSSOENDPOINT", "StopList: $bussoEndpoint")
  }

  private val disposables = CompositeDisposable()

  override fun start() {
    disposables.add(
        locationObservable
            .filter(::isLocationEvent)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleLocationEvent, ::handleError)
    )
  }

  private fun handleLocationEvent(locationEvent: LocationEvent) {
    when (locationEvent) {
      is LocationNotAvailable -> useViewBinder {
        displayErrorMessage("Location Not Available")
      }
      is LocationData -> useLocation(locationEvent.location)
    }
  }

  private fun useLocation(location: GeoLocation) {
    disposables.add(
        bussoEndpoint
            .findBusStopByLocation(location.latitude, location.longitude, 500)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(::mapBusStop)
            .subscribe(::displayBusStopList, ::handleError)
    )
  }

  private fun displayBusStopList(busStopList: List<BusStopViewModel>) {
    useViewBinder {
      displayBusStopList(busStopList)
    }
  }

  private fun handleError(throwable: Throwable) {
    useViewBinder {
      displayErrorMessage("Error: ${throwable.localizedMessage}")
    }
  }

  override fun stop() {
    disposables.clear()
  }

  private fun isLocationEvent(locationEvent: LocationEvent) =
      locationEvent !is LocationPermissionRequest && locationEvent !is LocationPermissionGranted

  override fun onBusStopSelected(busStopViewModel: BusStopViewModel) {
    navigator.navigateTo(
        FragmentFactoryDestination(
            fragmentFactory = { bundle ->
              BusArrivalFragment().apply {
                arguments = bundle
              }
            },
            anchorId = R.id.anchor_point,
            withBackStack = "BusArrival",
            bundle = bundleOf(
                BUS_STOP_ID to busStopViewModel.stopId
            )
        )
    )
  }

  override fun retry() {
    start()
  }
}
