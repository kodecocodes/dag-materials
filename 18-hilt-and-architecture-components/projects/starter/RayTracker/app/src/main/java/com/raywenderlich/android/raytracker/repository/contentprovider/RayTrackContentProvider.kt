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

package com.raywenderlich.android.raytracker.repository.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.raywenderlich.android.raytracker.conf.Config
import com.raywenderlich.android.raytracker.repository.dao.TrackDao
import com.raywenderlich.android.raytracker.repository.db.TrackDatabase
import com.raywenderlich.android.raytracker.repository.util.toTrackData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class RayTrackContentProvider : ContentProvider(), CoroutineScope {

  companion object {
    private const val TRACK_DIR_INDICATOR = 1
    private const val TRACK_ITEM_INDICATOR = 2
    private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
      addURI(TrackDBMetadata.AUTHORITY, TrackDBMetadata.TrackData.PATH, TRACK_DIR_INDICATOR)
      addURI(TrackDBMetadata.AUTHORITY, "${TrackDBMetadata.TrackData.PATH}/#", TRACK_ITEM_INDICATOR)
    }
  }

  private lateinit var trackDatabase: TrackDatabase
  private lateinit var trackDao: TrackDao

  override fun onCreate(): Boolean {
    trackDatabase = getRoomDatabase()
    trackDao = trackDatabase.trackDao()
    return true
  }

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
    // We just support delete everything
    var deleted: Int
    runBlocking {
      deleted = trackDao.deleteAll()
      context?.contentResolver?.notifyChange(uri, null)
    }
    return deleted
  }

  override fun getType(uri: Uri): String = when (URI_MATCHER.match(uri)) {
    TRACK_DIR_INDICATOR -> TrackDBMetadata.TrackData.MIME_TYPE_DIR
    TRACK_ITEM_INDICATOR -> TrackDBMetadata.TrackData.MIME_TYPE_ITEM
    else -> throw IllegalArgumentException("$uri not valid")
  }

  override fun insert(uri: Uri, values: ContentValues?): Uri? {
    if (URI_MATCHER.match(uri) != TRACK_DIR_INDICATOR) {
      throw IllegalArgumentException("You can only add items using the DIR mime type")
    }
    var returnUri: Uri? = null
    values?.toTrackData()?.let { newTrackData ->
      runBlocking {
        val newId = trackDao.insert(newTrackData)
        returnUri = ContentUris.withAppendedId(
            TrackDBMetadata.TrackData.CONTENT_URI,
            newId
        )
        context?.contentResolver?.notifyChange(returnUri!!, null)
      }
    }
    return returnUri
  }

  override fun query(
      uri: Uri, projection: Array<String>?, selection: String?,
      selectionArgs: Array<String>?, sortOrder: String?
  ): Cursor? {
    var queryCursor: TrackCursor
    // You only support getting all the items
    runBlocking {
      queryCursor = TrackCursor(trackDao.list())
    }
    return queryCursor
  }

  override fun update(
      uri: Uri, values: ContentValues?, selection: String?,
      selectionArgs: Array<String>?
  ): Int {
    throw UnsupportedOperationException("You don't need to update")
  }

  private fun getRoomDatabase(): TrackDatabase =
      Room.databaseBuilder(
          context!!,
          TrackDatabase::class.java,
          Config.DB.DB_NAME
      ).fallbackToDestructiveMigration().build()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO
}