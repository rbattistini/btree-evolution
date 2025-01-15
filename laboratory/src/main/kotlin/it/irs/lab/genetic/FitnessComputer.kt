package it.irs.lab.genetic

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jenetics.Genotype
import it.irs.evolution.BTreeGene
import it.irs.lab.btree.GridWorldBTree
import it.irs.lab.env.GridView.toAscii
import it.irs.lab.env.GridWorld
import it.irs.lab.fsm.robotState.Idle
import it.irs.lab.sim.GridWorldSim
import it.irs.lab.sim.GridWorldSimStatistics
import it.irs.lab.sim.SimRunner.getEnv
import it.irs.lab.sim.Stopwatch
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.fsm.StateMachine
import kotlin.collections.plus
import kotlin.random.Random

/**
 * A weighted combination of multiple goals.
 *
 * - Positive phototaxis: reward for reaching a light source;
 * - ObstacleAvoidance: reward for avoiding obstacles;
 * - Efficiency: reward for efficient completion of tasks;
 * - Collision: penalty for collision;
 * - Negative phototaxis: penalty for reaching a light source;
 * - Complexity: penalty for tree depth and tree size.
 */
class FitnessComputer(
  val maxTreeSize: Int,
  val treeComplexityPenaltyWeight: Double,
  val backtrackingWeight: Double,
  val collisionPenaltyWeight: Double,
  val phototaxisRewardWeight: Double,
  val simRunsPerFitnessEval: Int,
  val maxSimSteps: Int,
  val virtualTime: Long,
  val deltaTime: Int,
  defaultSeed: Int,
  val dimensions: Int,
  val numObstacles: Int,
  val maxValidationAttempts: Int,
  val neighbourRadius: Int,
) {
  val ff: (Genotype<BTreeGene<GridWorld>>) -> Double = { gt ->
    Stopwatch
      .measure("Running $simRunsPerFitnessEval simulations...") {
        evalSimNTimes(gt.gene().allele()).values.sum()
      }.result
  }

  private val logger: KLogger = KotlinLogging.logger {}
  private val random = Random(defaultSeed)

  fun evalSimNTimes(btree: BehaviorTree<GridWorld>): Map<String, Double> =
    (1..simRunsPerFitnessEval)
      .map { evalSim(btree) }
      .fold(emptyMap<String, Double>()) { acc, map ->
        map.entries.fold(acc) { innerAcc, (key, value) ->
          innerAcc + (key to (innerAcc.getOrDefault(key, 0.0) + value))
        }
      }.mapValues { (_, total) -> total / simRunsPerFitnessEval }

  fun evalSim(btree: GridWorldBTree): Map<String, Double> {
    logger.debug { "Building the environment..." }
    logger.debug { "Btree in use \n${btree.string}" }
    val env =
      getEnv(btree, random, dimensions, numObstacles, maxValidationAttempts, neighbourRadius)
    logger.debug { "\n${ env.space.toAscii()}" }
    val fsm = StateMachine(Idle(env))
    val sim = GridWorldSim(fsm, virtualTime, deltaTime, maxSimSteps)
    return evalSim0(sim)
  }

  fun evalSim0(sim: GridWorldSim): Map<String, Double> {
    val executedSim =
      Stopwatch
        .measure("Running the simulation...") {
          sim.execute()
        }.result

    logger.debug { "Showing the statistics..." }
    val stats = executedSim.finalStatistics() as GridWorldSimStatistics
    logger.debug { stats }

    val totalSteps = stats.totalSteps
    val tcp =
      treeComplexityPenalty(stats.treeSize, maxTreeSize) *
        treeComplexityPenaltyWeight
    val bp =
      backtrackingPenalty(stats.backtrackingSteps, totalSteps) *
        backtrackingWeight
    val cp =
      collisionPenalty(stats.collisionSteps, totalSteps) *
        collisionPenaltyWeight
    val pr =
      phototaxisReward(stats.initialDistanceToLight, stats.finalDistanceToLight) *
        phototaxisRewardWeight

    logger.debug { "Tree complexity penalty: \t$tcp" }
    logger.debug { "Backtracking penalty: \t\t$bp" }
    logger.debug { "Collision penalty: \t\t\t$cp" }
    logger.debug { "Phototaxis reward: \t\t\t$pr" }
    logger.debug { "Fitness \t\t\t\t\t\t${tcp + bp + cp + pr}" }

    return buildMap {
      put("treeComplexityPenalty", tcp)
      put("backtrackingPenalty", bp)
      put("collisionPenalty", cp)
      put("phototaxisReward", pr)
    }
  }

  companion object {
    val phototaxisReward: (Int, Int) -> Double = { startDistance, finalDistance ->
      val distanceDifference = startDistance - finalDistance
      val maxDistance = startDistance.toDouble()
      when {
        distanceDifference <= 0 -> 0.0
        else -> distanceDifference.toDouble() / maxDistance
      }
    }

    val collisionPenalty: (Int, Int) -> Double = { collisionSteps, totalSteps ->
      -collisionSteps.toDouble() / totalSteps.toDouble()
    }

    val backtrackingPenalty: (Int, Int) -> Double = collisionPenalty

    val treeComplexityPenalty: (Int, Int) -> Double = { treeSize, maxTreeSize ->
      when {
        treeSize >= maxTreeSize -> -1.0
        else -> 0.0
      }
    }
  }
}
