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

import com.raywenderlich.android.randomfunnumber.fakes.FakeCallback
import com.raywenderlich.android.randomfunnumber.fakes.FakeNumberGenerator
import com.raywenderlich.android.randomfunnumber.fakes.StubFunNumberEndpoint
import com.raywenderlich.android.randomfunnumber.model.FunNumber
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class FunNumberServiceImplTest {

  private lateinit var objectUnderTest: FunNumberServiceImpl
  private lateinit var numberGenerator: FakeNumberGenerator
  private lateinit var funNumberEndpoint: StubFunNumberEndpoint
  private lateinit var testScheduler: TestScheduler

  @Before
  fun setUp() {
    testScheduler = TestScheduler()
    numberGenerator = FakeNumberGenerator()
    funNumberEndpoint = StubFunNumberEndpoint()
    objectUnderTest = FunNumberServiceImpl(
        numberGenerator,
        funNumberEndpoint,
        testScheduler,
        Schedulers.trampoline()
    )
  }


  @After
  fun release() {
    objectUnderTest.stop()
  }

  @Test
  fun `when you invoke randomFunNumber you get a funNumber`() {
    numberGenerator.nextNumber = 123
    val fakeCallback = FakeCallback<FunNumber>()
    objectUnderTest.randomFunNumber(fakeCallback)
    testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
    val received = fakeCallback.callbackParameter
    assertNotNull(received)
    if (received != null) {
      assertEquals(received.number, 123)
      assertTrue(received.found)
      assertEquals(received.text, "Number is: 123")
      assertEquals(received.type, "validType")
    } else {
      fail("Something wrong!")
    }
  }

}