plugins {
  id("com.android.application")
  kotlin("android")
}
android {
  compileSdk = AppConfig.compileSdk
  buildToolsVersion = AppConfig.buildToolsVersion

  defaultConfig {
    applicationId = AppConfig.appId
    minSdk = AppConfig.minSdk
    targetSdk = AppConfig.targetSdk
    versionCode = AppConfig.versionCode
    versionName = AppConfig.versionName

    testInstrumentationRunner = AppConfig.androidTestInstrumentation
  }
  buildTypes {
    getByName(BuildTypes.RELEASE.code) {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile(AppConfig.proguardDefaultFile),
        AppConfig.proguardConsumerRules
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}
dependencies {
  kotlinStdLib()
  implementationOf(
    androidUI(
      appCompat = true,
      constraintLayout = true,
      material = true,
      corektx = true
    ),
    retrofitDeps(gsonconverter = true, rx2converter = true),
    rxDeps(rxandroid = true)
  )
  implementation(project(":libs:location:rx"))
  implementation(project(":libs:ui:navigation"))
  testImplementationOf(
    junitDeps(ext = true),
    robolectricDeps()
  )
  androidTestImplementationOf(
    espressoDeps()
  )
}