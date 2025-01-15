package it.irs.lab.experiment

import it.irs.lab.experiment.config.ExperimentConfig
import it.irs.lab.experiment.config.ExperimentResult
import it.irs.lab.experiment.config.GenerationStatistics
import kotlinx.serialization.Serializable

@Serializable
data class Experiment(
  val name: String,
  val time: Long,
  val generationStats: List<GenerationStatistics>,
  val parameters: ExperimentConfig,
  val globalStats: ExperimentResult,
) {
  fun logResult(
    generation: Long,
    population: Int,
    bestFitness: Double,
    averageFitness: Double,
    time: Int,
  ): Experiment {
    val result = GenerationStatistics(generation, population, bestFitness, averageFitness, time)
    return copy(generationStats = generationStats + result)
  }

  companion object {
    const val DEFAULT_EXP_PATH = "./experiment"

    val path: (String) -> String = { name ->
      "$DEFAULT_EXP_PATH/$name.json"
    }

    fun create(
      config: ExperimentConfig,
      result: ExperimentResult,
    ): Experiment {
      val experimentNameGenerator = NameGenerator()
      val currentTime = System.currentTimeMillis()
      return Experiment(
        experimentNameGenerator.randomName(),
        currentTime,
        emptyList(),
        config,
        result,
      )
    }
  }
}
