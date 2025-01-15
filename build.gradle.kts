plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kover)
  alias(libs.plugins.dokkatoo)
  alias(libs.plugins.kotlinx)
}

dependencies {
  implementation(project(":simulation"))
  implementation(project(":evolution"))
  implementation(project(":laboratory"))

  testImplementation(kotlin("test"))

  kover(project(":laboratory"))
  kover(project(":simulation"))

  dokkatoo(project(":evolution"))
  dokkatoo(project(":laboratory"))
  dokkatoo(project(":simulation"))
}

dokkatoo {
  moduleName.set("btree evolution")
}

kover.reports {
  verify {
    rule {
      bound {
        minValue.set(40)
        maxValue.set(80)
      }
    }
  }
}

kotlin {
  jvmToolchain(21)
  compilerOptions {
    allWarningsAsErrors = true
    freeCompilerArgs.addAll(
      listOf(
        "-opt-in=kotlin.RequiresOptIn",
      ),
    )
  }
}
