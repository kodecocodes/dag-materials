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

package com.raywenderlich.android.busso.ui.view.busstop

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.busso.R
import com.raywenderlich.android.busso.ui.events.OnItemSelectedListener

/**
 * The ViewHolder for the List of BusStop
 */
class BusStopItemViewHolder(
    itemView: View,
    private val onItemSelectedListener: OnItemSelectedListener<BusStopViewModel>? = null
) : RecyclerView.ViewHolder(itemView) {

  private val busStopNameTextView: TextView = itemView.findViewById(R.id.bus_stop_item_name)
  private val busStopIndicatorTextView: TextView = itemView.findViewById(R.id.bus_stop_indicator)
  private val busStopDirectionTextView: TextView =
      itemView.findViewById(R.id.bus_stop_item_direction)
  private val busStopDistanceTextView: TextView =
      itemView.findViewById(R.id.bus_stop_item_distance)

  lateinit var busStopListModel: BusStopViewModel

  fun bind(position: Int, itemViewModel: BusStopViewModel) {
    busStopListModel = itemViewModel
    busStopNameTextView.text = itemViewModel.stopName
    busStopIndicatorTextView.text = itemViewModel.stopIndicator
    busStopDistanceTextView.text = itemViewModel.stopDistance
    if (itemViewModel.stopDirection.isBlank()) {
      busStopDirectionTextView.visibility = View.GONE
    } else {
      busStopDirectionTextView.run {
        text = itemViewModel.stopDirection
        visibility = View.VISIBLE
      }
    }
    onItemSelectedListener?.run {
      itemView.setOnClickListener {
        invoke(position, itemViewModel)
      }
    }
  }
}