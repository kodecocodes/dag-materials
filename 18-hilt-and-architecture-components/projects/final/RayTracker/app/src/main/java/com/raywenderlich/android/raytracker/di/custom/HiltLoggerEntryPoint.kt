package com.raywenderlich.android.raytracker.di.custom

import com.raywenderlich.android.raytracker.logging.HiltLogger
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn

@EntryPoint
@InstallIn(TrackRunningComponent::class)
interface HiltLoggerEntryPoint {

  fun logger(): HiltLogger
}