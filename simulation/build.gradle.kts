group = "it.irs"

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.dokkatoo)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.detekt)
  alias(libs.plugins.kover)
}

dependencies {
  implementation(libs.arrow.core)

  implementation(libs.kotlin.logging)
  implementation(libs.logback)
  implementation(libs.slf4jOverLogback)

  testImplementation(kotlin("test"))
  testImplementation(libs.kotest.runner)
  testImplementation(libs.kotest.assertions)
  testImplementation(libs.kotest.property)
  testImplementation(libs.mockk)
}

kotlin {
  compilerOptions {
    allWarningsAsErrors = true
    freeCompilerArgs.addAll(
      listOf(
        "-opt-in=kotlin.RequiresOptIn",
      ),
    )
  }
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(21)
}
