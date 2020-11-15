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
package com.raywenderlich.android.busso.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.raywenderlich.android.busso.conf.BUSSO_SERVER_BASE_URL
import com.raywenderlich.android.busso.model.BusArrivals
import com.raywenderlich.android.busso.model.BusStop
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val CACHE_SIZE = 100 * 1024L // 100K

/**
 * The interface which abstracts the endpoint for the EverBus applicationø
 */
interface BussoEndpoint {

  /**
   * This is the endpoint which returns the list of Bus stop for a given
   * location and radius
   */
  @GET("${BUSSO_SERVER_BASE_URL}findBusStop/{lat}/{lng}")
  fun findBusStopByLocation(
      @Path("lat") latitude: Double,
      @Path("lng") longitude: Double,
      @Query("radius") radius: Int
  ): Single<List<BusStop>>

  /**
   * This is the endpoint which returns the list of Arrival for a given BusStop grouped
   * by line
   */
  @GET("$BUSSO_SERVER_BASE_URL/findBusArrivals/{stopId}")
  fun findArrivals(
      @Path("stopId") stopId: String
  ): Single<BusArrivals>
}


fun provideBussoEndPoint(context: Context): BussoEndpoint {
  val cache = Cache(context.cacheDir, CACHE_SIZE)
  val okHttpClient = OkHttpClient.Builder()
      .cache(cache)
      .build()
  val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(BUSSO_SERVER_BASE_URL)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(
          GsonConverterFactory.create(
              GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
          )
      )
      .client(okHttpClient)
      .build()
  return retrofit.create(BussoEndpoint::class.java)
}