package it.irs.lab

import io.jenetics.EliteSelector
import io.jenetics.TournamentSelector
import it.irs.lab.ExperimentRunnerUtils.loadConfig
import it.irs.lab.ExperimentRunnerUtils.runGeneticAlgorithm
import it.irs.lab.experiment.EvolutionEngineBuilder

fun main(args: Array<String>) {
  val expCfg = loadConfig(args)
  val engineBuilder = EvolutionEngineBuilder(expCfg)
  val engine =
    engineBuilder
      .builder()
      .survivorsSelector(EliteSelector(expCfg.eliteCount))
      .offspringSelector(TournamentSelector(expCfg.tournamentSampleSize))
      .build()

  runGeneticAlgorithm(expCfg, engine)
}
