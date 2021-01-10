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
package com.raywenderlich.android.raytracker.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.raytracker.R
import com.raywenderlich.android.raytracker.di.custom.HiltLoggerEntryPoint
import com.raywenderlich.android.raytracker.di.custom.TrackRunningComponentManager
import com.raywenderlich.android.raytracker.notification.createNotificationChannel
import com.raywenderlich.android.raytracker.repository.entity.TrackData
import com.raywenderlich.android.raytracker.service.TrackingService
import com.raywenderlich.android.raytracker.state.TrackerRunning
import com.raywenderlich.android.raytracker.state.TrackerState
import dagger.hilt.EntryPoints
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var trackRunningComponentManager: TrackRunningComponentManager

  val locationViewModel: CurrentLocationViewModel by viewModels()

  private lateinit var trackListAdapter: TrackListAdapter
  private lateinit var trackDataRecyclerView: RecyclerView
  private lateinit var startStopButton: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    createNotificationChannel()
    startStopButton = findViewById(R.id.startStopTrackingButton)
    trackDataRecyclerView = findViewById(R.id.location_recyclerview)
    initRecyclerView(trackDataRecyclerView)
    handleButtonState(locationViewModel.locationEvents().value)
    handleTrackDataList(locationViewModel.storedLocations().value)
  }

  override fun onStart() {
    super.onStart()
    with(locationViewModel) {
      findViewById<Button>(R.id.clearDataTrackingButton).setOnClickListener {
        clearDb()
      }
      storedLocations().observe(this@MainActivity, ::handleTrackDataList)
      locationEvents().observe(this@MainActivity, ::handleButtonState)
      start()
    }
  }


  private fun handleTrackDataList(trackDataList: List<TrackData>?) {
    trackListAdapter.submitList(trackDataList)
    trackDataRecyclerView.smoothScrollToPosition(0)
  }

  private fun handleButtonState(newState: TrackerState?) {
    with(startStopButton) {
      if (newState is TrackerRunning) {
        with(trackRunningComponentManager) {
          startWith(System.currentTimeMillis())
          with(newState.location) {
            EntryPoints.get( // HERE
                trackRunningComponent, HiltLoggerEntryPoint::class.java
            ).logger().log("Lat: $latitude Long: $longitude")
          }
        }
        text = getString(R.string.stop_tracking)
        setOnClickListener {
          stopService(Intent(this@MainActivity, TrackingService::class.java))
        }
      } else {
        trackRunningComponentManager.stop()
        text = getString(R.string.start_tracking)
        setOnClickListener {
          startService(Intent(this@MainActivity, TrackingService::class.java))
        }
      }
    }
  }

  override fun onStop() {
    super.onStop()
    trackRunningComponentManager.stop()
  }

  private fun initRecyclerView(trackDataRecyclerView: RecyclerView) {
    trackListAdapter = TrackListAdapter()
    trackDataRecyclerView.apply {
      val viewManager = LinearLayoutManager(trackDataRecyclerView.context)
      layoutManager = viewManager
      adapter = trackListAdapter
    }
  }
}