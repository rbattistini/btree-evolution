package it.irs.lab.sim

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jenetics.Genotype
import it.irs.evolution.BTreeGene
import it.irs.lab.ExperimentConfig.MAX_SIM_STEPS
import it.irs.lab.ExperimentConfig.SIM_RUNS
import it.irs.lab.btree.GridWorldBTree
import it.irs.lab.env.GridView.toAscii
import it.irs.lab.env.GridWorld
import it.irs.lab.fsm.robotState.Idle
import it.irs.lab.sim.SimRunner.getEnv
import it.irs.simulation.fsm.StateMachine

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
class FitnessComputer {
  val ff: (Genotype<BTreeGene<GridWorld>>) -> Double = { gt ->
    Stopwatch
      .measure("Running $SIM_RUNS simulations...") {
        (1..SIM_RUNS).sumOf { evalSim(gt.gene().allele()) } / SIM_RUNS
      }.result
  }

  private val logger: KLogger = KotlinLogging.logger {}

  private fun evalSim(btree: GridWorldBTree): Double {
    logger.info { "Building the environment..." }
    logger.info { "Btree in use \n${btree.string}" }
    val env = getEnv(btree)
    logger.info { "\n${ env.space.toAscii()}" }
    val fsm = StateMachine(Idle(env))
    val sim = GridWorldSim(fsm, maxSteps = MAX_SIM_STEPS)
    return evalSim0(sim)
  }

  fun evalSim0(sim: GridWorldSim): Double {
    val executedSim =
      Stopwatch
        .measure("Running the simulation...") {
          sim.execute()
        }.result

    logger.info { "Showing the statistics..." }
    val simStat = executedSim.finalStatistics() as GridWorldSimStatistics
    logger.info { simStat }

    val treeComplexityPenalty = simStat.btreeComplexityPenalty()
    val idlePenalty = simStat.idlePenalty()
    val revisitedCellPenalty = simStat.revisitedCellPenalty()
//    val clearNavigationReward = simStat.clearNavigationReward()
    val obstacleAvoidanceReward = simStat.obstacleAvoidanceReward()
    val phototaxisReward = simStat.phototaxisReward()

    logger.info { "Tree complexity penalty: \t$treeComplexityPenalty" }
    logger.info { "Idle penalty: \t\t\t\t$idlePenalty" }
    logger.info { "Revisited cell penalty: \t\t$revisitedCellPenalty" }
//    logger.info { "Clear navigation reward: \t$clearNavigationReward" }
    logger.info { "Obstacle avoidance reward: \t$obstacleAvoidanceReward" }
    logger.info { "Phototaxis reward: \t\t\t$phototaxisReward" }

    val fitness =
      treeComplexityPenalty +
        idlePenalty +
        revisitedCellPenalty +
//        clearNavigationReward +
        obstacleAvoidanceReward +
        phototaxisReward
    logger.info { "Fitness \t\t\t\t\t\t$fitness" }
    return fitness
  }
}
