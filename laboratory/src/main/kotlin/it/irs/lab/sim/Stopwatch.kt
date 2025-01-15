package it.irs.lab.sim

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

object Stopwatch {
  private val logger: KLogger = KotlinLogging.logger {}
  const val NANO_TO_MILLIS = 1_000_000

  data class MeasurementResults<T>(
    val result: T,
    val duration: Long,
  ) : Comparable<MeasurementResults<*>> {
    override fun compareTo(other: MeasurementResults<*>): Int = duration.compareTo(other.duration)
  }

  fun <T> measure(
    msg: String = "",
    block: () -> T,
  ): MeasurementResults<T> {
    val startTime = System.nanoTime()
    val result = block()
    val duration = (System.nanoTime() - startTime) / NANO_TO_MILLIS
    if (msg.isNotBlank()) logger.debug { msg }
    logger.debug { "Took $duration ms" }
    return MeasurementResults(result, duration)
  }
}
