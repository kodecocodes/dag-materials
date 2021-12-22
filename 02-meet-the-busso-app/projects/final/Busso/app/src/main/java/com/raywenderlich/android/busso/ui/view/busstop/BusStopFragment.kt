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

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.android.busso.R
import com.raywenderlich.android.busso.network.provideBussoEndPoint
import com.raywenderlich.android.busso.ui.events.OnItemSelectedListener
import com.raywenderlich.android.busso.ui.view.busarrival.BusArrivalFragment
import com.raywenderlich.android.busso.ui.view.busarrival.BusArrivalFragment.Companion.BUS_STOP_ID
import com.raywenderlich.android.location.api.model.*
import com.raywenderlich.android.location.api.permissions.GeoLocationPermissionChecker
import com.raywenderlich.android.location.rx.provideRxLocationObservable
import com.raywenderlich.android.ui.navigation.FragmentFactoryDestination
import com.raywenderlich.android.ui.navigation.Navigator
import com.raywenderlich.android.ui.navigation.NavigatorImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * The Fragment which displays the list of BusStop close to the
 */
class BusStopFragment : Fragment() {

  private val disposables = CompositeDisposable()
  private lateinit var locationManager: LocationManager
  private lateinit var locationObservable: Observable<LocationEvent>
  private val grantedPermissionChecker = object : GeoLocationPermissionChecker {
    override val isPermissionGiven: Boolean = true
  }
  private lateinit var busStopRecyclerView: RecyclerView
  private lateinit var navigator: Navigator
  private val busStopAdapter =
      BusStopListAdapter(object :
          OnItemSelectedListener<BusStopViewModel> {
        override fun invoke(pos: Int, busStopViewModel: BusStopViewModel) {
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
      })

  override fun onAttach(context: Context) {
    super.onAttach(context)
    locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    locationObservable = provideRxLocationObservable(locationManager, grantedPermissionChecker)
    navigator = NavigatorImpl(context as Activity)
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_busstop_layout, container, false).apply {
    busStopRecyclerView = findViewById(R.id.busstop_recyclerview)
    initRecyclerView(busStopRecyclerView)
  }

  override fun onStart() {
    super.onStart()
    subscribeToLocation()
  }

  override fun onStop() {
    disposables.clear()
    super.onStop()
  }

  private fun isLocationEvent(locationEvent: LocationEvent) =
      locationEvent !is LocationPermissionRequest && locationEvent !is LocationPermissionGranted

  private fun handleLocationEvent(locationEvent: LocationEvent) {
    when (locationEvent) {
      is LocationNotAvailable -> displayLocationNotAvailable()
      is LocationData -> useLocation(locationEvent.location)
    }
  }

  private fun useLocation(location: GeoLocation) {
    context?.let { ctx ->
      disposables.add(
          provideBussoEndPoint(ctx)
              .findBusStopByLocation(location.latitude, location.longitude, 500)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .map(::mapBusStop)
              .subscribe(busStopAdapter::submitList, ::handleBusStopError)
      )
    }
  }

  private fun handleBusStopError(error: Throwable) {
    // TODO Handle errors
  }

  private fun displayLocationNotAvailable() {
    Snackbar.make(
        busStopRecyclerView,
        R.string.warning_location_not_available,
        Snackbar.LENGTH_LONG
    )
        .setAction(R.string.message_retry) {
          subscribeToLocation()
        }
        .show()
  }

  private fun handleError(error: Throwable) {
    Snackbar.make(
        busStopRecyclerView,
        R.string.error_problem_getting_location,
        Snackbar.LENGTH_LONG
    )
        .setAction(R.string.message_retry) {
          subscribeToLocation()
        }
        .show()
  }

  private fun subscribeToLocation() {
    disposables.add(
        locationObservable
            .filter(::isLocationEvent)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleLocationEvent, ::handleError)
    )
  }

  private fun initRecyclerView(busStopRecyclerView: RecyclerView) {
    busStopRecyclerView.apply {
      val viewManager = LinearLayoutManager(busStopRecyclerView.context)
      layoutManager = viewManager
      adapter = busStopAdapter
    }
  }
}