package it.irs.lab.sim

import it.irs.lab.ExperimentConfig.DEFAULT_VIRTUAL_TIME
import it.irs.lab.ExperimentConfig.DELTA_TIME
import it.irs.lab.ExperimentConfig.MAX_SIM_STEPS
import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.collisionSteps
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.idleSteps
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.revisitingSteps
import it.irs.lab.env.GridExtensions.lightCells
import it.irs.lab.env.GridExtensions.startPositions
import it.irs.lab.env.GridView.toAscii
import it.irs.lab.env.GridWorld
import it.irs.lab.env.GridWorld.Companion.lightColorToFollow
import it.irs.lab.fsm.robotState.GoalReached
import it.irs.simulation.Simulation
import it.irs.simulation.fsm.StateMachine

data class GridWorldSim(
  override val fsm: StateMachine<GridWorld>,
  override val virtualTime: Long = DEFAULT_VIRTUAL_TIME,
  val maxSteps: Int = MAX_SIM_STEPS,
  val logResult: Boolean = false,
) : Simulation<GridWorld> {
  override fun tick(env: GridWorld): Simulation<GridWorld> =
    copy(
      fsm = fsm.tick(env),
      virtualTime = virtualTime + DELTA_TIME,
    )

  override fun isTerminated(): Boolean = virtualTime >= maxSteps || fsm.currentState is GoalReached

  private fun logResultAsAscii(env: GridWorld) {
    println(
      env.space
        .toAscii(
          setOf(
            env
              .activeRobot()
              .position()!!,
          ),
        ),
    )
    println()
  }

  override fun stepStatistics(env: GridWorld) {
    if (logResult) {
//      logResultAsImage()
      logResultAsAscii(env)
    }
  }

  override fun finalStatistics(): GridWorldSimStatistics {
    val env = fsm.currentState.env
    val robot = env.activeRobot()
    val defaultSteps = 0

    return GridWorldSimStatistics(
      initialDistanceToLight = computeInitialDistanceToLight(env),
      finalDistanceToLight = computeFinalDistanceToLight(env),
      lightReached = fsm.currentState is GoalReached,
      collisionSteps = robot.collisionSteps() ?: defaultSteps,
      idleSteps = robot.idleSteps() ?: defaultSteps,
      revisitedCellSteps = robot.revisitingSteps() ?: defaultSteps,
      totalSteps = virtualTime,
      treeSize = robot.btree.size,
    )
  }

  private fun computeFinalDistanceToLight(env: GridWorld): Int {
    val grid = env.space
    val startPosition = grid.startPositions().first()
    val lastPosition = env.activeRobot().position() ?: startPosition
    val lightPosition = grid.lightCells(lightColorToFollow).first()
    return lastPosition.manhattanDistance(lightPosition)
  }

  private fun computeInitialDistanceToLight(env: GridWorld): Int {
    val grid = env.space
    val startPosition = grid.startPositions().first()
    val lightPosition = grid.lightCells(lightColorToFollow).first()
    return startPosition.manhattanDistance(lightPosition)
  }
}
