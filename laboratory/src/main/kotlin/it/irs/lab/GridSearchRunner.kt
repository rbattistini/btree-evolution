package it.irs.lab

import io.jenetics.EliteSelector
import io.jenetics.TournamentSelector
import it.irs.lab.ExperimentRunnerUtils.loadConfig
import it.irs.lab.ExperimentRunnerUtils.runGeneticAlgorithm
import it.irs.lab.experiment.EvolutionEngineBuilder
import it.irs.lab.experiment.config.ExperimentConfig

@Suppress("MagicNumber")
object GridSearchRunner {
  val mutationProbability = listOf(0.5, 0.6, 0.7, 0.8)
  val crossoverProbability = listOf(0.5, 0.6, 0.7, 0.8)
  val selectorParams = listOf(Pair(1, 3), Pair(2, 4), Pair(3, 5))
  val gridParams = listOf(Pair(5, 7), Pair(7, 12), Pair(9, 18))
  val treeParams = listOf(Triple(2, 2, 3), Triple(3, 3, 4), Triple(4, 4, 5))
  val simSteps = listOf(50, 70, 90)

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

    for (mp in mutationProbability) {
      for (cp in crossoverProbability) {
        for (sp in selectorParams) {
          for (gp in gridParams) {
            for (tp in treeParams) {
              for (simStep in simSteps) {
                val currentConfig =
                  expCfg.copy(
                    mutationProbability = mp,
                    crossoverProbability = cp,
                    eliteCount = sp.first,
                    tournamentSampleSize = sp.second,
                    gridDimensions = gp.first,
                    gridObstacles = gp.second,
                    maxTreeDepth = tp.first,
                    minTreeChildren = tp.second,
                    maxTreeChildren = tp.third,
                    maxSimSteps = simStep,
                  )
                runGA(currentConfig)
              }
            }
          }
        }
      }
    }
  }
}

fun main(args: Array<String>) {
  GridSearchRunner.startGridSearch(args)
}
