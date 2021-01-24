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

package com.raywenderlich.android.raytracker.service

import com.raywenderlich.android.location.api.model.LocationData
import com.raywenderlich.android.location.api.model.LocationEvent
import com.raywenderlich.android.raytracker.state.TrackerRunning
import com.raywenderlich.android.raytracker.state.TrackerStateManager
import com.raywenderlich.android.raytracker.state.TrackerStopped
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/** Tracker implementation */
@ServiceScoped
class TrackerImpl @Inject constructor(
    private val trackerStateManager: TrackerStateManager,
    private val locationFlow: @JvmSuppressWildcards Flow<LocationEvent>
) : Tracker, CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO

  private lateinit var currentJob: Job

  override fun start(): Boolean {
    val currentState = trackerStateManager.trackerState.value
    if (currentState == null || currentState == TrackerStopped) {
      currentJob = launch {
        locationFlow.filter {
          it is LocationData
        }.map {
          it as LocationData
        }.collect {
          trackerStateManager.update(TrackerRunning(it.location))
        }
      }
      return true
    }
    return false
  }

  override fun stop(): Boolean {
    val currentState = trackerStateManager.trackerState.value
    if (currentState != TrackerStopped) {
      trackerStateManager.update(TrackerStopped)
      currentJob.cancel()
      return true
    }
    return false
  }
}