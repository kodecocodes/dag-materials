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

import android.database.AbstractCursor
import com.raywenderlich.android.raytracker.repository.contentprovider.TrackDBMetadata.TrackData.Columns.ID
import com.raywenderlich.android.raytracker.repository.contentprovider.TrackDBMetadata.TrackData.Columns.LATITUDE
import com.raywenderlich.android.raytracker.repository.contentprovider.TrackDBMetadata.TrackData.Columns.LONGITUDE
import com.raywenderlich.android.raytracker.repository.contentprovider.TrackDBMetadata.TrackData.Columns.TIMESTAMP
import com.raywenderlich.android.raytracker.repository.entity.TrackData

class TrackCursor(
    val data: List<TrackData>
) : AbstractCursor() {
  override fun getCount(): Int = data.size

  override fun getColumnNames(): Array<String> = arrayOf(
      ID,
      TIMESTAMP,
      LATITUDE,
      LONGITUDE,
  )

  fun getTrackData() = data[position]

  override fun getString(column: Int): String {
    throw IllegalAccessException("Use getTrackData() instead")
  }

  override fun getShort(column: Int): Short {
    throw IllegalAccessException("Use getTrackData() instead")
  }

  override fun getInt(column: Int): Int {
    throw IllegalAccessException("Use getTrackData() instead")
  }

  override fun getLong(column: Int): Long {
    throw IllegalAccessException("Use getTrackData() instead")
  }

  override fun getFloat(column: Int): Float {
    throw IllegalAccessException("Use getTrackData() instead")
  }

  override fun getDouble(column: Int): Double {
    throw IllegalAccessException("Use getTrackData() instead")
  }

  override fun isNull(column: Int): Boolean {
    throw IllegalAccessException("Use getTrackData() instead")
  }

  private fun current(): TrackData = data[position]
}