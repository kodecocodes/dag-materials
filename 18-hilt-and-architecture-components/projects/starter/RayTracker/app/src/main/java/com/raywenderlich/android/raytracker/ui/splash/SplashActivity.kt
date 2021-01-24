package com.raywenderlich.android.raytracker.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.android.location.permission.util.LocationPermissionHelper
import com.raywenderlich.android.raytracker.R
import com.raywenderlich.android.raytracker.ui.main.MainActivity
import com.raywenderlich.android.ui.navigation.ActivityIntentDestination
import com.raywenderlich.android.ui.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

  val splashViewModel: SplashViewModel by viewModels()

  @Inject
  lateinit var locationPermissionHelper: LocationPermissionHelper

  @Inject
  lateinit var navigator: Navigator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    makeFullScreen()
    setContentView(R.layout.activity_splash)
    locationPermissionHelper.onLocationGranted = {
      navigator.navigateTo(
          ActivityIntentDestination(
              Intent(this, MainActivity::class.java)
          )
      )
      overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
      finish()
    }
  }

  override fun onStart() {
    super.onStart()
    with(splashViewModel) {
      start()
      locations().observe(this@SplashActivity) {
        locationPermissionHelper.handlePermissionEvent(it)
      }
    }
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<String>,
      grantResults: IntArray
  ) {
    locationPermissionHelper.handlePermissionsResult(requestCode, grantResults)
  }

  private fun makeFullScreen() {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
    supportActionBar?.hide()
  }
}