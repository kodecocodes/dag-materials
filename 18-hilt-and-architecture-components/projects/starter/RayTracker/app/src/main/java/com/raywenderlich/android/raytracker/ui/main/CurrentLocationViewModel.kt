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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.raywenderlich.android.raytracker.repository.contentprovider.TrackDataHelper
import com.raywenderlich.android.raytracker.repository.entity.TrackData
import com.raywenderlich.android.raytracker.state.TrackerState
import com.raywenderlich.android.raytracker.state.TrackerStateManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

/** The ViewModel for the MainActivity */
@ExperimentalCoroutinesApi
class CurrentLocationViewModel(
    application: Application,
    private val trackerStateManager: TrackerStateManager,
    private val trackDataHelper: TrackDataHelper
) : AndroidViewModel(application) {

  private val _locationEvents = MutableLiveData<TrackerState>()
  private val _storedLocations = MutableLiveData<List<TrackData>>()

  private val locationObserver: Observer<in TrackerState> =
      Observer { trackerState: TrackerState ->
        _locationEvents.value = trackerState
      }

  fun start() {
    trackerStateManager.trackerState.observeForever(locationObserver)
    trackDataHelper.start { trackDataList ->
      _storedLocations.value = trackDataList
    }
  }

  fun clearDb() {
    trackDataHelper.clearDb()
  }

  fun locationEvents(): LiveData<TrackerState> = _locationEvents

  fun storedLocations(): LiveData<List<TrackData>> = _storedLocations

  override fun onCleared() {
    super.onCleared()
    trackDataHelper.stop()
    trackerStateManager.trackerState.removeObserver(locationObserver)
  }
}