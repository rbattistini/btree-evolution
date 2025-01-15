package it.irs.lab.experiment

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jenetics.Genotype
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import io.jenetics.stat.DoubleMomentStatistics
import it.irs.evolution.BTreeGene
import it.irs.lab.env.GridWorld
import it.irs.lab.experiment.Experiment.Companion.path
import it.irs.lab.experiment.config.DefaultConfig.defaultRules
import it.irs.lab.experiment.config.ExperimentConfig
import it.irs.lab.experiment.config.ExperimentResult
import it.irs.lab.experiment.serializer.Json
import it.irs.lab.sim.Stopwatch.NANO_TO_MILLIS
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.randomization.UnaryRandomTool
import java.math.RoundingMode

class ExperimentLogger(
  config: ExperimentConfig,
) {
  private var experiment: Experiment = Experiment.create(config, ExperimentResult())

  fun logEvolution(res: EvolutionResult<BTreeGene<GridWorld>, Double>) {
    val generation = res.generation()
    val population = res.population().length()
    val bestFitness = roundOffDecimal(res.bestFitness())
    val averageFitness = roundOffDecimal(res.population().map { it.fitness() }.average())
    val timeMillis = res.durations().evolveDuration.nano / NANO_TO_MILLIS

    logger.info {
      "Generation: $generation, Population: $population, " +
        "Best fitness: $bestFitness, Average fitness: $averageFitness, " +
        "Duration: $timeMillis ms"
    }

    experiment =
      experiment.logResult(generation, population, bestFitness, averageFitness, timeMillis)
  }

  fun saveFinalResults(
    bestGenotype: Genotype<BTreeGene<GridWorld>>,
    stats: EvolutionStatistics<Double, DoubleMomentStatistics>,
    registry: LeafNodeRegistry<GridWorld>,
    mutations: List<UnaryRandomTool<GridWorld>>,
  ) {
    val finalResult =
      ExperimentResult(
        bestBTree = bestGenotype.gene().allele().string,
        leafNodes = registry.nodes.keys.toList(),
        mutations = mutations.map { it.name },
        reparations = defaultRules.map { it.name },
        generations = experiment.generationStats.size,
        altered = stats.altered().toIntMoments(),
        killed = stats.killed().toIntMoments(),
        invalids = stats.invalids().toIntMoments(),
        phenotypeAge = stats.phenotypeAge().toLongMoments(),
        bestFitness = stats.fitness().toDoubleMoments(),
        alterDuration = stats.alterDuration().toDoubleMoments(),
        evolveDuration = stats.evolveDuration().toDoubleMoments(),
        selectionDuration = stats.selectionDuration().toDoubleMoments(),
        evalDuration = stats.evaluationDuration().toDoubleMoments(),
      )
    experiment = experiment.copy(globalStats = finalResult)

    val expPath = path(experiment.name)
    logger.info { "Experiment logged at: $expPath" }
    Json.save(experiment, expPath)
  }

  companion object {
    private val logger: KLogger = KotlinLogging.logger {}
    const val FITNESS_ROUNDING_SCALE = 1

    fun roundOffDecimal(number: Double): Double =
      number.toBigDecimal().setScale(FITNESS_ROUNDING_SCALE, RoundingMode.UP).toDouble()
  }
}
