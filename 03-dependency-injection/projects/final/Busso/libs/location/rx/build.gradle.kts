plugins {
  id("com.android.library")
  kotlin("android")
}
android {
  compileSdk = AppConfig.compileSdk
  buildToolsVersion = AppConfig.buildToolsVersion

  defaultConfig {
    minSdk = AppConfig.minSdk
    targetSdk = AppConfig.targetSdk

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

  api(project(":libs:location:api"))

  implementationOf(
    rxDeps(rxkotlin = true, rxandroid = true)
  )
  testImplementationOf(
    junitDeps(),
    robolectricDeps(),
    thirdPartyTestingDeps(mockitokotlin2 = true)
  )
}