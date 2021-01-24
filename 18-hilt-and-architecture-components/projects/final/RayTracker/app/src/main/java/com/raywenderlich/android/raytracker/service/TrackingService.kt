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

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.raywenderlich.android.raytracker.R
import com.raywenderlich.android.raytracker.notification.CHANNEL_ID
import com.raywenderlich.android.raytracker.state.TrackerState
import com.raywenderlich.android.raytracker.state.TrackerStateManager
import com.raywenderlich.android.raytracker.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TrackingService : LifecycleService() {

  @Inject
  lateinit var tracker: Tracker

  @Inject
  lateinit var trackerStateManager: TrackerStateManager

  private val trackStateObserver: Observer<TrackerState> = Observer { trackState ->
    updateNotification(trackState)
  }

  companion object {
    const val FOREGROUND_NOTIFICATION_ID = 1
  }

  lateinit var notificationBuilder: NotificationCompat.Builder

  override fun onCreate() {
    super.onCreate()
    notificationBuilder = createNotificationBuilder()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    if (tracker.start()) {
      trackerStateManager.trackerState.observe(this, trackStateObserver)
    }

    return Service.START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    if (tracker.stop()) {
      stopForeground(true)
      trackerStateManager.trackerState.removeObserver(trackStateObserver)
    }
  }

  private fun updateNotification(currentState: TrackerState) {
    notificationBuilder.setContentText("RayTracker Running:")
    startForeground(
        FOREGROUND_NOTIFICATION_ID, notificationBuilder
        .setContentText("Current State: $currentState")
        .build()
    )
  }

  @ExperimentalCoroutinesApi
  private fun createNotificationBuilder(): NotificationCompat.Builder {
    val intent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
    return NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .setContentTitle(resources.getString(R.string.notification_content))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setNotificationSilent()
        .setContentIntent(pendingIntent)
  }
}