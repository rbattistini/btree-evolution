package it.irs.lab.experiment.config

import kotlinx.serialization.Serializable

@Serializable
data class ExperimentConfig(
  val defaultSeed: Int = DefaultConfig.DEFAULT_SEED,
  // Engine Parameters
  val populationSize: Int = DefaultConfig.POP_SIZE,
  val maxPhenotypeAge: Long = DefaultConfig.MAX_PHENOTYPE_AGE,
  val mutationProbability: Double = DefaultConfig.MUTATION_PROB,
  val crossoverProbability: Double = DefaultConfig.CROSSOVER_PROBABILITY,
  val eliteCount: Int = DefaultConfig.ELITE_COUNT,
  val tournamentSampleSize: Int = DefaultConfig.TOURNAMENT_SAMPLE_SIZE,
  val steadyFitness: Int = DefaultConfig.STEADY_FITNESS,
  val maxGenerations: Long = DefaultConfig.MAX_GENERATIONS,
  val maxReparationAttempts: Int = DefaultConfig.REPARATION_ATTEMPTS,
  val leafNodes: String = "",
  // Simulation parameters
  val gridDimensions: Int = DefaultConfig.DEFAULT_DIMENSION,
  val gridObstacles: Int = DefaultConfig.DEFAULT_NUM_OBSTACLES,
  val maxGridValidationAttempts: Int = DefaultConfig.MAX_VALIDATION_ATTEMPTS,
  val maxNeighbourRadiusForGridValidation: Int = DefaultConfig.DEFAULT_NEIGHBOURS_RADIUS,
  val maxTreeDepth: Int = DefaultConfig.TREE_MAX_DEPTH,
  val minTreeChildren: Int = DefaultConfig.TREE_MIN_CHILDREN,
  val maxTreeChildren: Int = DefaultConfig.TREE_MAX_CHILDREN,
  val maxSimSteps: Int = DefaultConfig.MAX_SIM_STEPS,
  val deltaTime: Int = DefaultConfig.DELTA_TIME,
  val startVirtualTime: Long = DefaultConfig.VIRTUAL_TIME,
  // Fitness function parameters
  val phototaxisReward: Double = DefaultConfig.GOAL_REACHED_REWARD,
  val collisionPenalty: Double = DefaultConfig.COLLISION_PENALTY,
  val backtrackingPenalty: Double = DefaultConfig.BACKTRACKING_PENALTY,
  val treeComplexityPenalty: Double = DefaultConfig.TREE_COMPLEXITY_PENALTY,
  val maxTreeSize: Int = DefaultConfig.MAX_TREE_SIZE,
  val simRunsPerFitnessEval: Int = DefaultConfig.SIM_RUNS,
)
