import org.gradle.api.artifacts.dsl.DependencyHandler

object Kotlin {
  const val version = "1.6.10"
}

private fun kotlinDep() = object : ConfigDep {
  override val deps: List<String>
    get() = listOf("org.jetbrains.kotlin:kotlin-stdlib:${Kotlin.version}")
}

fun DependencyHandler.kotlinStdLib() {
  implementation {
    addDep(kotlinDep())
  }
}