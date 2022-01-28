/*
 * Copyright (c) 2022 Razeware LLC
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

package com.raywenderlich.android.busso.ui.view.busarrival

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.busso.R
import com.raywenderlich.android.busso.network.provideBussoEndPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * The Fragment for displaying the arrival time
 */
class BusArrivalFragment : Fragment() {

  private val disposables = CompositeDisposable()
  private lateinit var busArrivalRecyclerView: RecyclerView
  private val busArrivalsAdapter = BusArrivalListAdapter()

  private lateinit var busStopIndicator: TextView
  private lateinit var busStopName: TextView
  private lateinit var busStopDistance: TextView
  private lateinit var busStopDirection: TextView


  companion object {
    const val BUS_STOP_ID = "BUS_STOP_ID"
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_busarrival_layout, container, false).apply {
    busStopIndicator = findViewById(R.id.bus_stop_indicator)
    busStopName = findViewById(R.id.bus_stop_item_name)
    busStopDistance = findViewById(R.id.bus_stop_item_distance)
    busStopDirection = findViewById(R.id.bus_stop_item_direction)
    busArrivalRecyclerView = findViewById(R.id.busarrival_recyclerview)
    initRecyclerView(busArrivalRecyclerView)
  }

  private fun initRecyclerView(busStopRecyclerView: RecyclerView) {
    busArrivalRecyclerView.apply {
      val viewManager = LinearLayoutManager(busStopRecyclerView.context)
      layoutManager = viewManager
      adapter = busArrivalsAdapter
    }
  }

  override fun onStart() {
    super.onStart()
    getBusArrivals()
  }

  override fun onStop() {
    disposables.clear()
    super.onStop()
  }

  private fun getBusArrivals() {
    val busStopId = arguments?.getString(BUS_STOP_ID) ?: ""
    context?.let { ctx ->
      disposables.add(
          provideBussoEndPoint(ctx)
              .findArrivals(busStopId)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .map(::mapBusArrivals)
              .subscribe(::handleBusArrival, ::handleBusArrivalError)
      )
    }
  }

  private fun handleBusArrival(arrivals: BusArrivalsViewModel) {
    with(arrivals.busStop) {
      busStopIndicator.text = stopIndicator
      busStopName.text = stopName
      busStopDistance.text = stopDistance
      busStopDirection.text = stopDirection
    }
    busArrivalsAdapter.submitList(arrivals.arrivals)
  }

  private fun handleBusArrivalError(error: Throwable) {
    // TODO Handle errors
  }
}