fun androidComponentsTestDeps(
    fragment: Boolean = false
): List<String> = mutableListOf<String>().apply {
  if (fragment) {
    add("androidx.fragment:fragment-testing:1.4.0")
  }
}

fun junitDeps(
    core: Boolean = true,
    ext: Boolean = false
): List<String> = mutableListOf<String>().apply {
  if (core) {
    add("junit:junit:4.13.2")
  }
  if (ext) {
    add("androidx.test.ext:junit-ktx:1.1.3")
  }
}

fun espressoDeps(
    core: Boolean = true,
    intents: Boolean = false,
    contrib: Boolean = false
): List<String> = mutableListOf<String>().apply {
  if (core) {
    add("androidx.test.espresso:espresso-core:3.4.0")
  }
  if (contrib) {
    add("androidx.test.espresso:espresso-contrib:3.4.0")
  }
  if (intents) {
    add("androidx.test.espresso:espresso-intents:3.4.0")
  }
}

fun runnerDeps(
    runner: Boolean = true,
    rules: Boolean = false
): List<String> = mutableListOf<String>().apply {
  if (runner) {
    add("androidx.test:runner:1.2.0")
  }
  if (rules) {
    add("androidx.test:rules:1.2.0")
  }
}

fun robolectricDeps(
): List<String> = mutableListOf<String>().apply {
  add("org.robolectric:robolectric:4.3")
}

fun thirdPartyTestingDeps(
    mockitokotlin2: Boolean = false
): List<String> = mutableListOf<String>().apply {
  if (mockitokotlin2) {
    add("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
  }
}