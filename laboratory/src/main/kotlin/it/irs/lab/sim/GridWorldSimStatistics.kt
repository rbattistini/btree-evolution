package it.irs.lab.sim

import it.irs.lab.ExperimentConfig.COLLISION_PENALTY
import it.irs.lab.ExperimentConfig.GOAL_REACHED_REWARD
import it.irs.lab.ExperimentConfig.IDLE_PENALTY
import it.irs.lab.ExperimentConfig.MAX_TREE_SIZE
import it.irs.lab.ExperimentConfig.REVISITED_CELL_PENALTY
import it.irs.lab.ExperimentConfig.TREE_SIZE_PENALTY
import it.irs.simulation.SimulationStatistics
import kotlin.math.max
import kotlin.math.pow

data class GridWorldSimStatistics(
  val initialDistanceToLight: Int,
  val finalDistanceToLight: Int,
  val lightReached: Boolean,
  val collisionSteps: Int,
  val idleSteps: Int,
  val revisitedCellSteps: Int,
  val totalSteps: Long,
  val treeSize: Int,
) : SimulationStatistics {
  fun phototaxisReward(): Double {
    val distanceDifference = initialDistanceToLight - finalDistanceToLight

    return when {
      distanceDifference > 0 -> {
        val progressFactor = (distanceDifference / initialDistanceToLight).toDouble()
        GOAL_REACHED_REWARD * progressFactor.pow(MOVING_NEARER_REWARD_EXP)
      }
      distanceDifference < 0 -> {
        val regressionFactor = (-distanceDifference / initialDistanceToLight).toDouble()
        -GOAL_REACHED_REWARD * regressionFactor.pow(MOVING_AWAY_PENALTY_EXP)
      }
      else -> {
        NO_PROGRESS_PENALTY
      }
    }
  }

  fun obstacleAvoidanceReward(): Double {
    val safeSteps = totalSteps - collisionSteps
    return (-collisionSteps * COLLISION_PENALTY) + safeSteps
  }

//  fun clearNavigationReward(): Long = totalSteps

  fun idlePenalty(): Double = -idleSteps * IDLE_PENALTY

  fun revisitedCellPenalty(): Double = -(revisitedCellSteps * REVISITED_CELL_PENALTY)

  fun btreeComplexityPenalty(): Double = -TREE_SIZE_PENALTY * max(0, (treeSize - MAX_TREE_SIZE))

  companion object {
    const val NO_PROGRESS_PENALTY = -0.1
    const val MOVING_AWAY_PENALTY_EXP = 1.5
    const val MOVING_NEARER_REWARD_EXP = 2.0
  }
}
