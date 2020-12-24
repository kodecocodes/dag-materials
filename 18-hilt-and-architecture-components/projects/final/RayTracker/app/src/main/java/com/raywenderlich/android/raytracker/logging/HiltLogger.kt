package com.raywenderlich.android.raytracker.logging

import android.util.Log
import com.raywenderlich.android.raytracker.di.custom.TrackRunnningScoped
import javax.inject.Inject

@TrackRunnningScoped
class HiltLogger @Inject constructor() {
  fun log(message: String) {
    Log.d("HILT_LOGGING", "${this} -> ${message}")
  }
}