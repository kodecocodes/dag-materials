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

import com.raywenderlich.android.busso.model.BusStop
import com.raywenderlich.android.location.api.model.GeoLocation
import junit.framework.Assert.assertEquals
import org.junit.Test

class BusStopMapperKtTest {

  @Test
  fun mapBusStop_givenCompleteBusStop_returnsCompleteBusStopViewModel() {
    val inputBusStop = BusStop(
      "id",
      "stopName",
      GeoLocation(1.0, 2.0),
      "direction",
      "indicator",
      123F
    )
    val expectedViewModel = BusStopViewModel(
      "id",
      "stopName",
      "direction",
      "indicator",
      "123 m"
    )
    assertEquals(expectedViewModel, mapBusStop(inputBusStop))
  }

  @Test
  fun mapBusStop_givenBusStopMissingDirection_returnsBusStopViewModelEmptyDiretion() {
    val inputBusStop = BusStop(
      "id",
      "stopName",
      GeoLocation(1.0, 2.0),
      null,
      "indicator",
      123F
    )
    val expectedViewModel = BusStopViewModel(
      "id",
      "stopName",
      "",
      "indicator",
      "123 m"
    )
    assertEquals(expectedViewModel, mapBusStop(inputBusStop))
  }

  @Test
  fun mapBusStop_givenBusStopMissingIndicator_returnsBusStopViewModelEmptyIndicator() {
    val inputBusStop = BusStop(
      "id",
      "stopName",
      GeoLocation(1.0, 2.0),
      "direction",
      null,
      123F
    )
    val expectedViewModel = BusStopViewModel(
      "id",
      "stopName",
      "direction",
      "",
      "123 m"
    )
    assertEquals(expectedViewModel, mapBusStop(inputBusStop))
  }

  @Test
  fun mapBusStop_givenBusStopMissingDistance_returnsBusStopViewModelDistanceIndicator() {
    val inputBusStop = BusStop(
      "id",
      "stopName",
      GeoLocation(1.0, 2.0),
      "direction",
      "indicator",
      null
    )
    val expectedViewModel = BusStopViewModel(
      "id",
      "stopName",
      "direction",
      "indicator",
      "--"
    )
    assertEquals(expectedViewModel, mapBusStop(inputBusStop))
  }

  @Test
  fun testMapBusStop_listOfBusStop_returnsListOfBusStopViewModel() {
    val inputBusStop1 = BusStop(
      "id1",
      "stopName1",
      GeoLocation(1.0, 2.0),
      "direction1",
      "indicator1",
      123F
    )
    val inputBusStop2 = BusStop(
      "id2",
      "stopName2",
      GeoLocation(1.0, 2.0),
      "direction2",
      "indicator2",
      234F
    )
    val expectedViewModel1 = BusStopViewModel(
      "id1",
      "stopName1",
      "direction1",
      "indicator1",
      "123 m"
    )
    val expectedViewModel2 = BusStopViewModel(
      "id2",
      "stopName2",
      "direction2",
      "indicator2",
      "234 m"
    )
    val result = mapBusStop(listOf(inputBusStop1, inputBusStop2))
    assertEquals(listOf(expectedViewModel1, expectedViewModel2), result)
  }
}