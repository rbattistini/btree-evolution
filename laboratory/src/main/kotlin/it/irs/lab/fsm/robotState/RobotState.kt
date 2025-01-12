package it.irs.lab.fsm.robotState

import it.irs.lab.blackboard.RobotWrapper.RobotGenericStorage.readPoints
import it.irs.lab.blackboard.RobotWrapper.RobotGenericStorage.updatePoint
import it.irs.lab.blackboard.RobotWrapper.RobotMovement.teleportTo
import it.irs.lab.blackboard.RobotWrapper.RobotState.markRobotAsIdle
import it.irs.lab.blackboard.RobotWrapper.RobotState.markRobotAsMoving
import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.addCollisionStep
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.addRevisitedCellStep
import it.irs.lab.env.GridExtensions.obstacles
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot

abstract class RobotState : RSState {
  fun tickActiveRobot(env: GridWorld): GridWorld {
    val rootState = env.activeRobot().btree.tick(env)
    logger.debug { "Execution result:\n ${rootState.stackTrace().joinToString("\n")}" }
    return rootState.env
  }

  fun checkIfMoving(env: GridWorld): GridWorld {
    val currentRobot = env.activeRobot()
    val oldRobotPosition = this.env.activeRobot().position()
    val currentRobotPosition = currentRobot.position()

    val updatedRobot =
      if (oldRobotPosition != currentRobotPosition) {
        currentRobot.markRobotAsMoving()
      } else {
        currentRobot.markRobotAsIdle()
      }
    return env.updateActiveRobot(updatedRobot) ?: env
  }

  fun checkIfRevisiting(env: GridWorld): GridWorld {
    val robot = env.activeRobot()
    val robotPosition = robot.position()
    val visitedPositions = robot.readPoints("visitedCells")

    val updatedRobot =
      if (visitedPositions?.contains(robotPosition) == true) {
        robot.addRevisitedCellStep()
      } else {
        robot.updatePoint("visitedCells", robotPosition)
      }
    return env.updateActiveRobot(updatedRobot) ?: env
  }

  fun checkIfColliding(env: GridWorld): GridWorld {
    val currentRobot = env.activeRobot()
    val oldRobotPosition = this.env.activeRobot().position()
    val currentRobotPosition = currentRobot.position()
    val otherRobotPositions =
      env.entities.values
        .filterIsInstance<Robot>()
        .map { it.position() }
        .filter { it != currentRobot.position() }
        .toSet()
    val obstacles = env.space.obstacles()
    val boundaries = env.space.boundaries(currentRobotPosition)

    val isColliding =
      otherRobotPositions.contains(currentRobotPosition) ||
        obstacles.contains(currentRobotPosition) ||
        boundaries.contains(currentRobotPosition)

    val updatedRobot =
      if (isColliding && oldRobotPosition != null) {
        currentRobot.addCollisionStep().teleportTo(oldRobotPosition)
      } else if (isColliding) {
        currentRobot.addCollisionStep()
      } else {
        currentRobot
      }
    return env.updateActiveRobot(updatedRobot) ?: env
  }
}
