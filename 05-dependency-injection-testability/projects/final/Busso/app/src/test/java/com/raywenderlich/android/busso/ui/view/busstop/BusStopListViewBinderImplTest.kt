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
import android.os.Build
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.busso.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BusStopListViewBinderImplTest {

  private lateinit var busStopListViewBinder: BusStopListViewBinder
  private lateinit var fakeBusStopItemSelectedListener: FakeBusStopItemSelectedListener
  private lateinit var activityController: ActivityController<Activity>
  private lateinit var testData: List<BusStopViewModel>

  @Before
  fun setUp() {
    activityController = Robolectric.buildActivity(
      Activity::class.java
    )
    testData = createTestData()
    fakeBusStopItemSelectedListener = FakeBusStopItemSelectedListener()
    busStopListViewBinder = BusStopListViewBinderImpl(fakeBusStopItemSelectedListener)
  }

  @Test
  fun displayBusStopList_whenInvoked_adapterContainsData() {
    val rootView = createLayoutForTest(activityController.get())
    with(busStopListViewBinder) {
      init(rootView)
      displayBusStopList(testData)
    }
    val adapter = rootView.findViewById<RecyclerView>(R.id.busstop_recyclerview).adapter!!
    assertEquals(3, adapter.itemCount)
  }

  @Test
  fun busStopItemSelectedListener_whenBusStopSelected_onBusStopSelectedIsInvoked() {
    val testData = createTestData()
    val activity = activityController.get()
    val rootView = createLayoutForTest(activity)
    activity.setContentView(rootView)
    activityController.create().start().visible();
    with(busStopListViewBinder) {
      init(rootView)
      displayBusStopList(testData)
    }
    rootView.findViewById<RecyclerView>(R.id.busstop_recyclerview).getChildAt(2).performClick()
    assertEquals(testData[2], fakeBusStopItemSelectedListener.onBusStopSelectedInvokedWith)
  }

  private class FakeBusStopItemSelectedListener :
    BusStopListViewBinder.BusStopItemSelectedListener {

    var onBusStopSelectedInvokedWith: BusStopViewModel? = null
    var retryInvoked = false

    override fun onBusStopSelected(busStopViewModel: BusStopViewModel) {
      onBusStopSelectedInvokedWith = busStopViewModel
    }

    override fun retry() {
      retryInvoked = true
    }
  }

  private fun createTestData() = listOf(
    createBusStopViewModelForTest("1"),
    createBusStopViewModelForTest("2"),
    createBusStopViewModelForTest("3"),
  )

  private fun createBusStopViewModelForTest(id: String) = BusStopViewModel(
    "stopId $id",
    "stopName $id",
    "stopDirection $id",
    "stopIndicator $id",
    "stopDistance $id"
  )

  private fun createLayoutForTest(context: Context) = LinearLayout(context)
    .apply {
      addView(RecyclerView(context).apply {
        id = R.id.busstop_recyclerview
      })
    }
}