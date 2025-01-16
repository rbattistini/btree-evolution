package it.irs.lab.fsm.robotState

import it.irs.lab.blackboard.RobotWrapper.RobotGenericStorage.readPoints
import it.irs.lab.blackboard.RobotWrapper.RobotGenericStorage.updatePoint
import it.irs.lab.blackboard.RobotWrapper.RobotState.markRobotAsIdle
import it.irs.lab.blackboard.RobotWrapper.RobotState.markRobotAsMoving
import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.addIdleStep
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.addRevisitedCellStep
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.node.BState

abstract class RobotState : RSState {
  fun tickActiveRobot(env: GridWorld): GridWorld {
    val rootState = env.activeRobot().btree.tick(env)
    logger.trace { "Execution result:\n ${rootState.stackTrace().joinToString("\n")}" }
    return if (rootState.state == BState.Failure) {
      env
    } else {
      rootState.env
    }
  }

  fun checkIfMoving(env: GridWorld): GridWorld {
    val currentRobot = env.activeRobot()
    val oldRobotPosition = this.env.activeRobot().position()
    val currentRobotPosition = currentRobot.position()

    val updatedRobot =
      if (oldRobotPosition != currentRobotPosition) {
        currentRobot.markRobotAsMoving()
      } else {
        currentRobot.markRobotAsIdle().addIdleStep()
      }
    return env.updateActiveRobot(updatedRobot) ?: env
  }

  fun writePreviousPosition(env: GridWorld): GridWorld {
    val previousRobot = this.env.activeRobot()
    val oldRobotPosition = previousRobot.position()
    val currentRobot = env.activeRobot()
    val updatedBlackboard = currentRobot.bb.write("previousPosition" to oldRobotPosition)
    val updatedRobot = currentRobot.copy(bb = updatedBlackboard)
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
}
