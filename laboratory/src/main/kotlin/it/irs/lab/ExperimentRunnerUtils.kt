package it.irs.lab

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.jenetics.Genotype
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import io.jenetics.engine.Limits.bySteadyFitness
import io.jenetics.util.ISeq
import it.irs.evolution.BTreeChromosome
import it.irs.evolution.BTreeConstraint
import it.irs.evolution.BTreeGene
import it.irs.lab.env.GridWorld
import it.irs.lab.experiment.ExperimentLogger
import it.irs.lab.experiment.config.DefaultConfig.createMutations
import it.irs.lab.experiment.config.DefaultConfig.createNodeFactory
import it.irs.lab.experiment.config.DefaultConfig.defaultNodeRegistry
import it.irs.lab.experiment.config.DefaultConfig.defaultRules
import it.irs.lab.experiment.config.DefaultConfig.defaultToolset
import it.irs.lab.experiment.config.ExperimentConfig
import it.irs.lab.experiment.serializer.Json
import it.irs.lab.genetic.Exp1GenePool.ofExp1
import it.irs.lab.genetic.Exp2GenePool.ofExp2
import it.irs.lab.genetic.FitnessComputer
import it.irs.simulation.btree.builder.BTreeRandomGenerator
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import kotlin.random.Random

object ExperimentRunnerUtils {
  private val logger: KLogger = logger {}

  fun loadConfig(args: Array<String>): ExperimentConfig {
    val configPath =
      if (args.isEmpty()) {
        logger.info { "Using default config" }
        null
      } else {
        val configName = args[0]
        logger.info { "Loaded config: $configName" }
        configName
      }
    return Json.load(configPath, ExperimentConfig())
  }

  fun genotype(
    registry: LeafNodeRegistry<GridWorld>,
    cfg: ExperimentConfig,
  ): Genotype<BTreeGene<GridWorld>> {
    val rndTreeGen =
      BTreeRandomGenerator(
        registry,
        cfg.maxTreeDepth,
        cfg.minTreeChildren,
        cfg.maxTreeChildren,
        random = Random(cfg.defaultSeed),
      )
    val startGene =
      BTreeGene(
        rndTreeGen.randomTree(),
        registry,
        defaultRules,
        rndTreeGen,
      )
    val chr =
      BTreeChromosome(
        ISeq.of(startGene),
        registry,
        defaultRules,
        rndTreeGen,
      )
    return Genotype.of(chr)
  }

  fun fitnessComputer(cfg: ExperimentConfig): FitnessComputer =
    FitnessComputer(
      maxTreeSize = cfg.maxTreeSize,
      treeComplexityPenaltyWeight = cfg.treeComplexityPenalty,
      backtrackingWeight = cfg.backtrackingPenalty,
      collisionPenaltyWeight = cfg.collisionPenalty,
      phototaxisRewardWeight = cfg.phototaxisReward,
      simRunsPerFitnessEval = cfg.simRunsPerFitnessEval,
      maxSimSteps = cfg.maxSimSteps,
      virtualTime = cfg.startVirtualTime,
      deltaTime = cfg.deltaTime,
      defaultSeed = cfg.defaultSeed,
      dimensions = cfg.gridDimensions,
      numObstacles = cfg.gridObstacles,
      maxValidationAttempts = cfg.maxGridValidationAttempts,
      neighbourRadius = cfg.maxNeighbourRadiusForGridValidation,
    )

  fun constraint(
    registry: LeafNodeRegistry<GridWorld>,
    cfg: ExperimentConfig,
  ): BTreeConstraint<GridWorld> {
    val nodeFactory = createNodeFactory(registry, Random(cfg.defaultSeed))
    return BTreeConstraint(
      defaultRules,
      defaultToolset,
      nodeFactory,
      cfg.maxReparationAttempts,
    )
  }

  fun resolveRegistry(cfg: ExperimentConfig): LeafNodeRegistry<GridWorld> =
    when (cfg.leafNodes) {
      "exp1" -> LeafNodeRegistry.ofExp1(Random(cfg.defaultSeed))
      "exp2" -> LeafNodeRegistry.ofExp2(Random(cfg.defaultSeed))
      else -> defaultNodeRegistry
    }

  fun runGeneticAlgorithm(
    cfg: ExperimentConfig,
    engine: Engine<BTreeGene<GridWorld>, Double>,
  ) {
    val registry = resolveRegistry(cfg)
    val mutations = createMutations(registry)

    val statistics = EvolutionStatistics.ofNumber<Double>()
    val experimentLogger = ExperimentLogger(cfg)

    val bestGenotype =
      engine
        .stream()
        .limit(bySteadyFitness(cfg.steadyFitness))
        .limit(cfg.maxGenerations)
        .peek(statistics)
        .peek { experimentLogger.logEvolution(it) }
        .collect(EvolutionResult.toBestGenotype())

    experimentLogger.saveFinalResults(bestGenotype, statistics, registry, mutations)
  }
}
