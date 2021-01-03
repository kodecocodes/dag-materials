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

package com.raywenderlich.android.randomfunnumber.business

import com.raywenderlich.android.di.scopes.ApplicationScoped
import com.raywenderlich.android.randomfunnumber.di.ApplicationModule
import com.raywenderlich.android.randomfunnumber.di.NetworkModule
import com.raywenderlich.android.randomfunnumber.di.SchedulersModule
import com.raywenderlich.android.randomfunnumber.fakes.FakeCallback
import com.raywenderlich.android.randomfunnumber.fakes.FakeNumberGenerator
import com.raywenderlich.android.randomfunnumber.fakes.StubFunNumberEndpoint
import com.raywenderlich.android.randomfunnumber.model.FunNumber
import com.raywenderlich.android.randomfunnumber.networking.FunNumberEndpoint
import com.raywenderlich.android.randomfunnumber.random.NumberGenerator
import com.raywenderlich.android.randomfunnumber.schedulers.IOScheduler
import com.raywenderlich.android.randomfunnumber.schedulers.MainScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(
    SchedulersModule::class,
    NetworkModule::class,
    ApplicationModule::class)
class FunNumberServiceImplHiltTest {

  @Inject
  lateinit var objectUnderTest: FunNumberServiceImpl

  @Inject
  @IOScheduler
  lateinit var testScheduler: Scheduler

  @BindValue
  @JvmField
  val funNumberEndPoint: FunNumberEndpoint = StubFunNumberEndpoint()

  @BindValue
  @JvmField
  val randomGenerator: NumberGenerator = FakeNumberGenerator().apply {
    nextNumber = 123
  }

  @get:Rule
  var hiltAndroidRule = HiltAndroidRule(this)

  @Before
  fun setUp() {
    hiltAndroidRule.inject()
  }

  @Test
  fun whenRandomFunNumberIsInvokedAResultIsReturned() {
    val fakeCallback = FakeCallback<FunNumber>()
    objectUnderTest.randomFunNumber(fakeCallback)
    (testScheduler as TestScheduler).advanceTimeBy(100, TimeUnit.MILLISECONDS)
    val received = fakeCallback.callbackParameter
    Assert.assertNotNull(received)
    if (received != null) {
      with(received) {
        assertEquals(number, 123)
        assertTrue(found)
        assertEquals(text, "Number is: 123")
        assertEquals(type, "validType")
      }
    } else {
      Assert.fail("Something wrong!")
    }
  }

  @Module
  @InstallIn(ApplicationComponent::class)
  object SchedulersModule {

    @Provides
    @ApplicationScoped
    @MainScheduler
    fun provideMainScheduler(): Scheduler = Schedulers.trampoline()

    @Provides
    @ApplicationScoped
    @IOScheduler
    fun provideIoScheduler(): Scheduler = TestScheduler()
  }
}