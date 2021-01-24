package com.raywenderlich.android.raytracker.di.custom

import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class TrackRunningComponentManager @Inject constructor(
    private val trackRunningBuilder: TrackRunningComponent.Builder
) {

  var trackRunningComponent: TrackRunningComponent? = null

  fun startWith(sessionId: Long) {
    if (trackRunningComponent == null) {
      trackRunningComponent = trackRunningBuilder
          .sessionId(sessionId)
          .build()
    }
  }

  fun stop() {
    if (trackRunningComponent != null) {
      trackRunningComponent = null
    }
  }
}