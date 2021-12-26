fun rxDeps(
    rxkotlin: Boolean = false,
    rxandroid: Boolean = false
): List<String> = mutableListOf<String>().apply {
  if (rxkotlin) {
    add("io.reactivex.rxjava2:rxkotlin:2.4.0")
  }
  if (rxandroid) {
    add("io.reactivex.rxjava2:rxandroid:2.1.0")
  }
}