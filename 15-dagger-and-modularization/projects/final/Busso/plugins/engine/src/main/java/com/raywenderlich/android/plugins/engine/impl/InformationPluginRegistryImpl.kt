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

package com.raywenderlich.android.plugins.engine.impl

import com.raywenderlich.android.di.scopes.ApplicationScope
import com.raywenderlich.android.plugins.api.ComplexInfoKey
import com.raywenderlich.android.plugins.api.InformationEndpoint
import com.raywenderlich.android.plugins.api.InformationPluginRegistry
import com.raywenderlich.android.plugins.api.InformationPluginSpec
import retrofit2.Retrofit
import javax.inject.Inject

/** Implementation for the InformationPluginRegistry */
@ApplicationScope
internal class InformationPluginRegistryImpl @Inject constructor(
  private val retrofit: Retrofit,
  informationPlugins: @JvmSuppressWildcards Map<ComplexInfoKey, InformationPluginSpec>
) : InformationPluginRegistry {

  private val endpoints = informationPlugins.keys.map { complexKey ->
    retrofit.create(complexKey.endpointClass.java as Class<*>) // 2
  }.map { endpoint ->
    endpoint as InformationEndpoint
  }.toList()

  override fun plugins(): List<InformationEndpoint> = endpoints
}
