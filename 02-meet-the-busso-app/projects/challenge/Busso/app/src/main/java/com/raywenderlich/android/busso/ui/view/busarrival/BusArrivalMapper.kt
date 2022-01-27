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

import com.raywenderlich.android.busso.model.BusArrival
import com.raywenderlich.android.busso.model.BusArrivalGroup
import com.raywenderlich.android.busso.model.BusArrivals
import com.raywenderlich.android.busso.ui.view.busstop.mapBusStop
import java.text.SimpleDateFormat
import java.util.*

/**
 * The ViewModel for the BusArrivals
 */
internal fun mapBusArrivals(busArrivals: BusArrivals): BusArrivalsViewModel =
    BusArrivalsViewModel(
        mapBusStop(busArrivals.busStop),
        busArrivals
            .arrivalGroups
            .map(::mapBusArrivalGroup)
    )

/**
 * Maps the BusArrivalGroup into a BusArrivalGroupViewModel adding some decorations
 */
internal fun mapBusArrivalGroup(busArrivalGroup: BusArrivalGroup): BusArrivalGroupViewModel {
  return BusArrivalGroupViewModel(
      lineName = busArrivalGroup.lineName,
      destination = busArrivalGroup.destination,
      arrivals = busArrivalGroup.arrivals.map(::mapBusArrival)
  )
}

/**
 * Maps the list of arrivals group into their viewmodel
 */
internal fun mapBusArrivalGroup(arrivals: List<BusArrivalGroup>): List<BusArrivalGroupViewModel> =
    arrivals.map(::mapBusArrivalGroup)

/**
 * Maps an arrival times group into its viewmodel
 */
internal fun mapBusArrival(arrival: BusArrival): BusArrivalViewModel =
    BusArrivalViewModel(
        expectedTime = expectedTime(arrival.expectedArrival),
        vehicleId = arrival.vehicleId ?: "-",
        destination = arrival.destinationName
    )

/**
 * Maps the list of arrival times group into their viewmodel
 */
fun mapBusArrival(arrivals: List<BusArrival>): List<BusArrivalViewModel> =
    arrivals.map(::mapBusArrival)

val DATE_FORMATTER = SimpleDateFormat("HH:mm", Locale.UK)

private fun expectedTime(expectedTime: Date) = DATE_FORMATTER.format(expectedTime)
