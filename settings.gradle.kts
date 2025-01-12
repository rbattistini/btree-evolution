pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
  id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.12"
}

rootProject.name = "btree-evolution"

include("simulation", "evolution", "laboratory")

gitHooks {
  preCommit {
    tasks("ktlintCheck")
  }
  commitMsg { conventionalCommits() }
  createHooks(true)
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}
