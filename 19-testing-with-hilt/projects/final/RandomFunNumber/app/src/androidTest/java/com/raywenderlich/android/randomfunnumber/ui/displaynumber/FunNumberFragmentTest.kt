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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.raywenderlich.android.randomfunnumber.R
import com.raywenderlich.android.randomfunnumber.business.FunNumberService
import com.raywenderlich.android.randomfunnumber.di.ActivityModule
import com.raywenderlich.android.randomfunnumber.fakes.FakeFunNumberService
import com.raywenderlich.android.randomfunnumber.model.FunNumber
import com.raywenderlich.android.randomfunnumber.util.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(ActivityModule.Bindings::class)
class FunNumberFragmentTest {

  @get:Rule
  var hiltAndroidRule = HiltAndroidRule(this)

  @BindValue
  @JvmField
  val funNumberService: FunNumberService = FakeFunNumberService()

  @Before
  fun setUp() {
    hiltAndroidRule.inject()
  }

  @Test
  fun whenButtonPushedSomeResultDisplayed() {
    (funNumberService as FakeFunNumberService).resultToReturn =
        FunNumber(123, "Funny Number", true, "testValue")
    launchFragmentInHiltContainer<FunNumberFragment>()
    onView(withId(R.id.refresh_fab_button)).perform(click())
    onView(withId(R.id.fun_number_output)).check(matches(withText("123")))
    onView(withId(R.id.fun_fact_output)).check(matches(withText("Funny Number")))
  }
}