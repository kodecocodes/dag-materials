fun androidUI(
  appCompat: Boolean = true,
  constraintLayout: Boolean = false,
  material: Boolean = false,
  corektx: Boolean = false
) = mutableListOf<String>().apply {
  if (appCompat) {
    add("androidx.appcompat:appcompat:1.4.0")
  }
  if (constraintLayout) {
    add("androidx.constraintlayout:constraintlayout:2.1.2")
  }
  if (material) {
    add("com.google.android.material:material:1.4.0")
  }
  if (corektx) {
    add("androidx.core:core-ktx:1.7.0")
  }
}