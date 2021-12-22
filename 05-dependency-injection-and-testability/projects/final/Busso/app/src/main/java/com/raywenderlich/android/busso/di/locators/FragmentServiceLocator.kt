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

package com.raywenderlich.android.busso.di.locators

import androidx.fragment.app.Fragment
import com.raywenderlich.android.busso.network.BussoEndpoint
import com.raywenderlich.android.busso.ui.view.busstop.BusStopListPresenter
import com.raywenderlich.android.busso.ui.view.busstop.BusStopListPresenterImpl
import com.raywenderlich.android.busso.ui.view.busstop.BusStopListViewBinder
import com.raywenderlich.android.busso.ui.view.busstop.BusStopListViewBinderImpl
import com.raywenderlich.android.location.api.model.LocationEvent
import com.raywenderlich.android.ui.navigation.Navigator
import io.reactivex.Observable

const val BUSSTOP_LIST_PRESENTER = "BusStopListPresenter"
const val BUSSTOP_LIST_VIEWBINDER = "BusStopListViewBinder"

val fragmentServiceLocatorFactory: (ServiceLocator) -> ServiceLocatorFactory<Fragment> =
  { fallbackServiceLocator: ServiceLocator ->
    { fragment: Fragment ->
      FragmentServiceLocator(fragment).apply {
        activityServiceLocator = fallbackServiceLocator
      }
    }
  }

class FragmentServiceLocator(
  val fragment: Fragment
) : ServiceLocator {

  var activityServiceLocator: ServiceLocator? = null
  var busStopListPresenter: BusStopListPresenter? = null
  var busStopListViewBinder: BusStopListViewBinder? = null

  @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
  override fun <A : Any> lookUp(name: String): A = when (name) {
    BUSSTOP_LIST_PRESENTER -> {
      if (busStopListPresenter == null) {
        val navigator: Navigator = activityServiceLocator!!.lookUp(NAVIGATOR)
        val locationObservable: Observable<LocationEvent> = activityServiceLocator!!.lookUp(
          LOCATION_OBSERVABLE
        )
        val bussoEndpoint: BussoEndpoint = activityServiceLocator!!.lookUp(BUSSO_ENDPOINT)
        busStopListPresenter = BusStopListPresenterImpl(
          navigator,
          locationObservable,
          bussoEndpoint
        )
      }
      busStopListPresenter
    }
    BUSSTOP_LIST_VIEWBINDER -> {
      if (busStopListViewBinder == null) {
        val busStopListPresenter: BusStopListPresenter = lookUp(BUSSTOP_LIST_PRESENTER)
        busStopListViewBinder = BusStopListViewBinderImpl(busStopListPresenter)
      }
      busStopListViewBinder
    }
    else -> activityServiceLocator?.lookUp<A>(name)
      ?: throw IllegalArgumentException("No component lookup for the key: $name")
  } as A
}