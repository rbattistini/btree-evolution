group = "it.irs"

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.dokkatoo)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.detekt)
  alias(libs.plugins.kover)
}

dependencies {
  implementation(libs.jenetics)
  implementation(libs.jenetics.prngine)

  implementation(project(":simulation"))

  implementation(libs.arrow.core)

  implementation(libs.kotlin.logging)
  implementation(libs.logback)
  implementation(libs.slf4jOverLogback)
}
