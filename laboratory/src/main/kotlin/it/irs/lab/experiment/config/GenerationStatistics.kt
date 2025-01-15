package it.irs.lab.experiment.config

import kotlinx.serialization.Serializable

@Serializable
data class GenerationStatistics(
  val generation: Long,
  val population: Int,
  val bestFitness: Double,
  val averageFitness: Double,
  val time: Int,
)
