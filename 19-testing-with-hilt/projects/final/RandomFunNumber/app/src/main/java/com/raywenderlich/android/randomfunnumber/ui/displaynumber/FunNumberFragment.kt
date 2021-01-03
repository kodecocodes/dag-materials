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
package com.raywenderlich.android.randomfunnumber.ui.displaynumber

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.raywenderlich.android.randomfunnumber.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FunNumberFragment : Fragment() {

  private lateinit var funNumberTextView: TextView
  private lateinit var funFactTextView: TextView
  val funNumberViewModel by viewModels<FunNumberViewModel>()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val ctx = container?.context ?: IllegalStateException("Context not available")
    return LayoutInflater.from(ctx as Context).inflate(R.layout.fragment_show_number, container, false).apply {
      funNumberTextView = findViewById(R.id.fun_number_output)
      funFactTextView = findViewById(R.id.fun_fact_output)
      findViewById<TextView>(R.id.fun_fact_output).setMovementMethod(LinkMovementMethod.getInstance())
      findViewById<ExtendedFloatingActionButton>(R.id.refresh_fab_button).setOnClickListener {
        funNumberViewModel.refreshNumber()
      }
    }
  }

  override fun onStart() {
    super.onStart()
    funNumberViewModel.numberFunFacts.observe(this) { funNumberResult ->
      if (funNumberResult.found) {
        funNumberTextView.text = funNumberResult.number.toString()
        funFactTextView.text = funNumberResult.text
      } else {
        funNumberTextView.text = getString(R.string.missing_number)
        funFactTextView.text = getString(R.string.error_message)
      }
    }
  }
}