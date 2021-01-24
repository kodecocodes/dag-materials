package com.raywenderlich.android.raytracker.di.custom

import dagger.BindsInstance
import dagger.hilt.DefineComponent
import dagger.hilt.android.components.ActivityComponent

@DefineComponent(parent = ActivityComponent::class)
@TrackRunningScoped
interface TrackRunningComponent {

  @DefineComponent.Builder
  interface Builder {
    fun sessionId(@BindsInstance sessionId: Long): Builder
    fun build(): TrackRunningComponent
  }
}