package it.irs.lab.experiment.serializer

import kotlinx.serialization.json.Json
import java.io.File

object Json {
  val encoderDecoder =
    Json {
      prettyPrint = true
      encodeDefaults = true
      ignoreUnknownKeys = true
    }

  inline fun <reified T> load(
    filePath: String?,
    default: T,
  ): T =
    if (filePath != null) {
      val file = File(filePath)
      if (file.exists()) {
        encoderDecoder.decodeFromString(file.readText())
      } else {
        default
      }
    } else {
      default
    }

  inline fun <reified T> save(
    experimentConfig: T,
    filePath: String,
  ) {
    val file = File(filePath)
    file.parentFile?.mkdirs()
    file.writeText(encoderDecoder.encodeToString(experimentConfig))
  }
}
