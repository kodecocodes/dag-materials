import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * If DEBUG is true you can access the list of depdendencies added with the related
 * buildType. If you copy and paste them in build.gradle you can check if some new
 * version is available and then remove the definition.
 * To do that just run:  ./gradlew :libs:ui:navigation:lint
 */
private val DEBUG = false

interface AddDeps {
  fun addDep(dep: ConfigDep)
}

private class AddDepsImpl : AddDeps {
  private val deps = mutableListOf<String>()
  override fun addDep(dep: ConfigDep) {
    deps.addAll(dep.deps)
  }

  fun addAllDeps(handler: DependencyHandler, type: String) {
    deps.forEach {
      handler.add(type, it)
    }
  }
}

interface ConfigDep {
  val deps: List<String>
}

fun DependencyHandler.handleDependency(type: String, init: AddDeps.() -> Unit) {
  val deps = AddDepsImpl()
  deps.init()
  deps.addAllDeps(this, type)
}

fun DependencyHandler.handleDependencyOf(type: String, vararg deps: List<String>) {
  deps.fold(mutableListOf<String>()) { acc, item ->
    acc.addAll(item)
    acc
  }.forEach {
    if (DEBUG) {
      println("$type(\"${it}\")")
    }
    add(type, it)
  }
}

fun DependencyHandler.api(init: AddDeps.() -> Unit) =
  handleDependency("api", init)

fun DependencyHandler.implementation(init: AddDeps.() -> Unit) =
  handleDependency("implementation", init)

fun DependencyHandler.testImplementation(init: AddDeps.() -> Unit) =
  handleDependency("testImplementation", init)

fun DependencyHandler.androidTestImplementation(init: AddDeps.() -> Unit) =
  handleDependency("androidTestImplementation", init)

fun DependencyHandler.implementationOf(vararg deps: List<String>) =
  handleDependencyOf("implementation", *deps)

fun DependencyHandler.testImplementationOf(vararg deps: List<String>) =
  handleDependencyOf("testImplementation", *deps)

fun DependencyHandler.androidTestImplementationOf(vararg deps: List<String>) =
  handleDependencyOf("androidTestImplementation", *deps)

fun DependencyHandler.apiOf(vararg deps: List<String>) =
  handleDependencyOf("api", *deps)
