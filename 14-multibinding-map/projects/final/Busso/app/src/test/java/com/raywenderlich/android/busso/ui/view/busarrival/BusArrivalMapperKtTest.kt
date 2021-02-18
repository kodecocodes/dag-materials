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

package com.raywenderlich.android.busso.ui.view.busarrival

import com.raywenderlich.android.busso.model.BusArrival
import com.raywenderlich.android.busso.model.BusArrivalGroup
import com.raywenderlich.android.busso.model.BusArrivals
import com.raywenderlich.android.busso.model.BusStop
import com.raywenderlich.android.busso.ui.view.busstop.BusStopViewModel
import com.raywenderlich.android.location.api.model.GeoLocation
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.*

class BusArrivalMapperKtTest {

  @Test
  fun mapBusArrivals_whenBusArrivalsComplete_returnsViewModelComplete() {
    val busStop = BusStop(
        "id",
        "stopName",
        GeoLocation(1.0, 2.0),
        "direction",
        "indicator",
        123F
    )
    val busArrivalGroup = BusArrivalGroup(
        "lineId",
        "lineName",
        "destinationName",
        listOf(
            BusArrival(
                "id1",
                "vehicle1",
                "lineId",
                "lineName",
                "destinationName",
                Calendar.getInstance().apply {
                  set(Calendar.HOUR, 12)
                  set(Calendar.MINUTE, 34)
                  set(Calendar.AM_PM, 0)
                }.time
            ),
            BusArrival(
                "id2",
                "vehicle2",
                "lineId",
                "lineName",
                "destinationName",
                Calendar.getInstance().apply {
                  set(Calendar.HOUR, 13)
                  set(Calendar.MINUTE, 5)
                  set(Calendar.AM_PM, 0)
                }.time
            )
        )
    )
    val busArrivals = BusArrivals(busStop, listOf(busArrivalGroup))
    val expected = BusArrivalsViewModel(
        BusStopViewModel(
            "id",
            "stopName",
            "direction",
            "indicator",
            "123 m"
        ),
        listOf(
            BusArrivalGroupViewModel(
                "lineName",
                "destinationName",
                listOf(
                    BusArrivalViewModel(
                        "12:34",
                        "vehicle1",
                        "destinationName"
                    ),
                    BusArrivalViewModel(
                        "13:05",
                        "vehicle2",
                        "destinationName"
                    )
                )
            )
        )
    )
    assertEquals(expected, mapBusArrivals(busArrivals))
  }

  @Test
  fun mapBusArrivalGroup_whenArrivalGroup_returnsViewModelWithArrivalsViewModels() {
    val busArrivalGroup = BusArrivalGroup(
        "lineId",
        "lineName",
        "destination",
        listOf(
            BusArrival(
                "id1",
                "vehicle1",
                "lineId",
                "lineName",
                "destinationName",
                Calendar.getInstance().apply {
                  set(Calendar.HOUR, 12)
                  set(Calendar.MINUTE, 34)
                  set(Calendar.AM_PM, 0)
                }.time
            ),
            BusArrival(
                "id2",
                "vehicle2",
                "lineId",
                "lineName",
                "destinationName",
                Calendar.getInstance().apply {
                  set(Calendar.HOUR, 13)
                  set(Calendar.MINUTE, 5)
                  set(Calendar.AM_PM, 0)
                }.time
            )
        )
    )
    val expected = BusArrivalGroupViewModel(
        "lineName",
        "destination",
        listOf(
            BusArrivalViewModel(
                "12:34",
                "vehicle1",
                "destinationName"
            ), BusArrivalViewModel(
            "13:05",
            "vehicle2",
            "destinationName"
        )
        )
    )
    assertEquals(expected, mapBusArrivalGroup(busArrivalGroup))
  }

  @Test
  fun mapBusArrival_whenBusArrivalIsComplete_retunsBusArrivalViewModelComplete() {
    val arrivalDate = Calendar.getInstance().apply {
      set(Calendar.HOUR, 12)
      set(Calendar.MINUTE, 34)
      set(Calendar.AM_PM, 0)
    }
    val busArrival = BusArrival(
        "id",
        "vehicleId",
        "lineId",
        "lineName",
        "destinationName",
        arrivalDate.time
    )
    val expected = BusArrivalViewModel(
        "12:34",
        "vehicleId",
        "destinationName"
    )
    assertEquals(expected, mapBusArrival(busArrival))
  }

  @Test
  fun mapBusArrival_whenBusArrivalVehicleMissing_retunsBusArrivalViewModelDefaultVehicle() {
    val arrivalDate = Calendar.getInstance().apply {
      set(Calendar.HOUR, 12)
      set(Calendar.MINUTE, 34)
      set(Calendar.AM_PM, 0)
    }
    val busArrival = BusArrival(
        "id",
        null,
        "lineId",
        "lineName",
        "destinationName",
        arrivalDate.time
    )
    val expected = BusArrivalViewModel(
        "12:34",
        "-",
        "destinationName"
    )
    assertEquals(expected, mapBusArrival(busArrival))
  }
}