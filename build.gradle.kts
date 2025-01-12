plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kover)
  alias(libs.plugins.dokkatoo)
}

dependencies {
  implementation(project(":evolution"))
  implementation(project(":simulation"))
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
        minValue.set(60)
        maxValue.set(75)
      }
    }
  }
}
