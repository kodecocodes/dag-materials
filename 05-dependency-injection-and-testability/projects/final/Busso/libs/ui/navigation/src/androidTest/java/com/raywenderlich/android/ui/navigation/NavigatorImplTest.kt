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

package com.raywenderlich.android.ui.navigation

import android.content.Intent
import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import com.raywenderlich.android.ui.navigation.util.ActivityTest
import com.raywenderlich.android.ui.navigation.util.FragmentTest
import com.raywenderlich.android.ui.navigation.util.FragmentTest.Companion.TEXTVIEW_ID
import com.raywenderlich.android.ui.navigation.util.SecondFragmentTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Test class for the ActivityNavigator implementation
 */
class NavigatorImplTest {

  @get:Rule
  val intentsTestRule = IntentsTestRule(ActivityTest::class.java)

  @Test
  fun navigateTo_usingActivityIntentDestination_intentForDestinationLaunched() {
    val activityNavigator = NavigatorImpl(intentsTestRule.activity)
    val testIntent = Intent(intentsTestRule.activity, ActivityTest::class.java).apply {
      putExtra("ExtraKey", "ExtraValue")
      addCategory("TEST_CATEGORY")
    }
    val destination = ActivityIntentDestination(testIntent)
    activityNavigator.navigateTo(destination)
    intended(IntentMatchers.hasCategories(setOf("TEST_CATEGORY")))
    intended(IntentMatchers.hasExtra("ExtraKey", "ExtraValue"))
  }

  @Test
  fun navigateTo_usingFragmentDestination_fragmentIsDisplayed() {
    val activityNavigator = NavigatorImpl(intentsTestRule.activity)
    val fragmentTest = FragmentTest()
    val fragmentDestination = FragmentDestination(fragmentTest, ActivityTest.ANCHOR_POINT_ID)
    activityNavigator.navigateTo(fragmentDestination)
    onView(withId(TEXTVIEW_ID)).check(matches(isDisplayed()))
  }

  @Test
  fun navigateTo_usingFragmentFactoryDestination_fragmentIsDisplayed() {
    val activityNavigator = NavigatorImpl(intentsTestRule.activity)
    val fragmentTestFactory = { bundle: Bundle? -> FragmentTest() }
    val fragmentDestination =
      FragmentFactoryDestination(fragmentTestFactory, ActivityTest.ANCHOR_POINT_ID)
    activityNavigator.navigateTo(fragmentDestination)
    onView(withId(TEXTVIEW_ID)).check(matches(isDisplayed()))
  }

  @Test
  fun navigateTo_usingActivityBackDestination_activityIsFinishing() {
    NavigatorImpl(intentsTestRule.activity)
      .navigateTo(ActivityBackDestination)
    assertTrue(intentsTestRule.activity.isFinishing)
  }

  @Test
  fun navigateTo_usingFragmentBackDestination_fragmentIsNotThere() {
    val activityNavigator = NavigatorImpl(intentsTestRule.activity)
    val fragmentTestFactory = { bundle: Bundle? -> FragmentTest() }
    val fragmentDestination =
      FragmentFactoryDestination(fragmentTestFactory, ActivityTest.ANCHOR_POINT_ID)
    activityNavigator.navigateTo(fragmentDestination)
    onView(withId(TEXTVIEW_ID)).check(matches(isDisplayed()))
    val fragmentDestination2 =
      FragmentDestination(
        SecondFragmentTest(), ActivityTest.ANCHOR_POINT_ID,
        "BackStack"
      )
    activityNavigator.navigateTo(fragmentDestination2)
    onView(withText("SecondTestFragment")).check(matches(isDisplayed()))
    activityNavigator.navigateTo(FragmentBackDestination)
    onView(withText("TestFragment")).check(matches(isDisplayed()))
  }
}