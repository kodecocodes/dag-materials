package com.raywenderlich.android.raytracker.ui.main

import android.content.Context
import com.raywenderlich.android.raytracker.R
import com.raywenderlich.android.raytracker.repository.entity.TrackData
import java.text.SimpleDateFormat
import java.util.*

/** ViewModel for TrackData */
data class TrackDataViewModel(
    val timestamp: String,
    val latitude: String,
    val longitude: String
)

private val DATE_FORMAT = SimpleDateFormat.getTimeInstance()

internal fun TrackData.asViewModel(context: Context): TrackDataViewModel = TrackDataViewModel(
    DATE_FORMAT.format(Date(timestamp)),
    context.getString(R.string.latitude_format, latitude),
    context.getString(R.string.longitude_format, longitude)
)