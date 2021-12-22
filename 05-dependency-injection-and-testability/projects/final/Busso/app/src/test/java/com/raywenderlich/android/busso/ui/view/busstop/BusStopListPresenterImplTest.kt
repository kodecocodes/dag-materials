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

import android.os.Build
import com.raywenderlich.android.busso.network.BussoEndpoint
import com.raywenderlich.android.location.api.model.LocationEvent
import com.raywenderlich.android.location.api.model.LocationNotAvailable
import com.raywenderlich.android.ui.navigation.Navigator
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BusStopListPresenterImplTest {

  lateinit var presenter: BusStopListPresenter
  lateinit var navigator: Navigator
  lateinit var locationObservable: PublishSubject<LocationEvent>
  lateinit var bussoEndpoint: BussoEndpoint
  lateinit var busStopListViewBinder: BusStopListViewBinder

  @Before
  fun setUp() {
    navigator = mock(Navigator::class.java)
    locationObservable = PublishSubject.create();
    bussoEndpoint = mock(BussoEndpoint::class.java)
    busStopListViewBinder = mock(BusStopListViewBinder::class.java)
    presenter = BusStopListPresenterImpl(
      navigator,
      locationObservable,
      bussoEndpoint,
    )
    presenter.bind(busStopListViewBinder)
  }

  @Test
  fun start_whenLocationNotAvailable_displayErrorMessageInvoked() {
    presenter.start()
    locationObservable.onNext(LocationNotAvailable("Provider"))
    verify(busStopListViewBinder).displayErrorMessage("Location Not Available")
  }
}