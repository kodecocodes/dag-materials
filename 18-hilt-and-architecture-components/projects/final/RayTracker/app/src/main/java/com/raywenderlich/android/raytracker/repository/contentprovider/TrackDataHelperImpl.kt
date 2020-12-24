package com.raywenderlich.android.raytracker.repository.contentprovider

import android.content.Context
import android.database.ContentObserver
import android.database.CursorWrapper
import android.os.Handler
import android.os.Looper
import com.raywenderlich.android.raytracker.repository.entity.TrackData
import com.raywenderlich.android.raytracker.repository.util.toContentValues
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** TrackDataHelper implementation */
class TrackDataHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TrackDataHelper {

  private lateinit var contentObserver: ContentObserver

  override fun start(callback: (List<TrackData>) -> Unit) {
    callback.invoke(currentTrackData())
    contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {

      override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        val wrappingCursor =
            context.contentResolver.query(
                TrackDBMetadata.TrackData.CONTENT_URI,
                emptyArray(),
                "",
                emptyArray(),
                ""
            ) as CursorWrapper
        callback.invoke((wrappingCursor.wrappedCursor as TrackCursor).data)
        wrappingCursor.close()
      }
    }
    context.contentResolver
        .registerContentObserver(
            TrackDBMetadata.TrackData.CONTENT_URI,
            true,
            contentObserver)
  }

  override fun insert(trackData: TrackData) {
    context.contentResolver.insert(
        TrackDBMetadata.TrackData.CONTENT_URI,
        trackData.toContentValues()
    )
  }

  override fun stop() {
    context.contentResolver.unregisterContentObserver(contentObserver)
  }

  override fun clearDb() {
    context.contentResolver.delete(TrackDBMetadata.TrackData.CONTENT_URI, null, emptyArray())
  }

  private fun currentTrackData(): List<TrackData> =
      context.contentResolver.query(
          TrackDBMetadata.TrackData.CONTENT_URI,
          emptyArray(),
          null,
          emptyArray(),
          null).use { cursor ->
        val wrappingCursor = cursor as CursorWrapper
        val currentData = (wrappingCursor.wrappedCursor as TrackCursor).data
        wrappingCursor.close()
        currentData
      }
}