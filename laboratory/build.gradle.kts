group = "it.irs"

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.dokkatoo)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.detekt)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotlinx)
}

dependencies {
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.jenetics)
  implementation(libs.jenetics.prngine)

  implementation(project(":evolution"))
  implementation(project(":simulation"))

  implementation(libs.arrow.core)

  implementation(libs.kotlin.logging)
  implementation(libs.logback)
  implementation(libs.slf4jOverLogback)

  testImplementation(kotlin("test"))
  testImplementation(libs.kotest.runner)
  testImplementation(libs.kotest.assertions)
  testImplementation(libs.kotest.property)
}

tasks.register<JavaExec>("runGA") {
  description = "Run the main GA class."
  group = "custom"

  standardOutput = System.out
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass = "${project.group}.lab.ExperimentRunnerKt"
}

tasks.register<JavaExec>("runRandomSelectorGA") {
  description = "Run the main GA class with a Montecarlo selector."
  group = "custom"

  standardOutput = System.out
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass = "${project.group}.lab.RandomSelectionBaselineKt"
}

tasks.register<JavaExec>("compareWithBaseline") {
  description = "Run the class to compare with the baseline."
  group = "custom"

  standardOutput = System.out
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass = "${project.group}.lab.HandcraftedBaselineKt"
}

tasks.test {
  useJUnitPlatform()
}
