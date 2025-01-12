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

tasks.register<JavaExec>("runGA") {
  description = "Run main class."
  group = "custom"

  standardOutput = System.out
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass = "${project.group}.lab.ExperimentRunnerKt"
}

tasks.jar {
  manifest.attributes["Main-Class"] = "it.irs.MainKt"
  val dependencies =
    configurations
      .runtimeClasspath
      .get()
      .map(::zipTree)
  from(dependencies)
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(21)
}
