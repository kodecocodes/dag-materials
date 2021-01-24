package com.raywenderlich.android.raytracker.repository.contentprovider

import com.raywenderlich.android.raytracker.repository.entity.TrackData

/** Helper for managing TrackData oContentProvider */
interface TrackDataHelper {

  fun start(callback: (List<TrackData>) -> Unit)

  fun insert(trackData: TrackData)

  fun stop()

  fun clearDb()
}