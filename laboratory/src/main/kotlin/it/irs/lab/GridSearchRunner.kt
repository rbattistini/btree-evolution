package it.irs.lab

import io.jenetics.EliteSelector
import io.jenetics.TournamentSelector
import it.irs.lab.ExperimentRunnerUtils.loadConfig
import it.irs.lab.ExperimentRunnerUtils.runGeneticAlgorithm
import it.irs.lab.experiment.EvolutionEngineBuilder
import it.irs.lab.experiment.config.ExperimentConfig

@Suppress("MagicNumber")
object GridSearchRunner {
  val gaOpsProbability = listOf(0.5, 0.6, 0.7, 0.8)
  val selectorParams = listOf(Pair(1, 3))
  val simParams = listOf(Triple(5, 7, 50), Triple(7, 12, 60), Triple(9, 18, 70))
  val treeParams = listOf(Triple(2, 2, 3), Triple(3, 3, 4))

  fun runGA(expCfg: ExperimentConfig) {
    val engineBuilder = EvolutionEngineBuilder(expCfg)
    val engine =
      engineBuilder
        .builder()
        .survivorsSelector(EliteSelector(expCfg.eliteCount))
        .offspringSelector(TournamentSelector(expCfg.tournamentSampleSize))
        .build()

    runGeneticAlgorithm(expCfg, engine)
  }

  @Suppress("NestedBlockDepth")
  fun startGridSearch(args: Array<String>) {
    val expCfg = loadConfig(args)

    for (gaOps in gaOpsProbability) {
      for (sp in selectorParams) {
        for (simP in simParams) {
          for (tp in treeParams) {
            val currentConfig =
              expCfg.copy(
                mutationProbability = gaOps,
                crossoverProbability = gaOps,
                eliteCount = sp.first,
                tournamentSampleSize = sp.second,
                gridDimensions = simP.first,
                gridObstacles = simP.second,
                maxTreeDepth = tp.first,
                minTreeChildren = tp.second,
                maxTreeChildren = tp.third,
                maxSimSteps = simP.third,
              )
            runGA(currentConfig)
          }
        }
      }
    }
  }
}

fun main(args: Array<String>) {
  GridSearchRunner.startGridSearch(args)
}
