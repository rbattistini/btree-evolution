package it.irs.lab.btree.node

import it.irs.lab.blackboard.RobotWrapper.RobotMovement.moveTo
import it.irs.lab.blackboard.RobotWrapper.RobotState.turnDirection
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.addCollisionStep
import it.irs.lab.btree.GridWorldANode
import it.irs.lab.btree.GridWorldTickResult
import it.irs.lab.btree.node.NodeUtils.isCollision
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.result.TickResult
import it.irs.simulation.space.grid.Direction

object MoveNodes {
  fun move(
    env: GridWorld,
    turnDirection: Direction?,
  ): GridWorldTickResult {
    val robot = env.activeRobot()
    val newPosition = robot.move(turnDirection)
    return if (isCollision(newPosition, env)) {
      val updatedRobot = robot.addCollisionStep()
      val updatedEnv = env.updateActiveRobot(updatedRobot)
      TickResult.fromResult(
        updatedEnv ?: env,
        BState.Failure,
      )
    } else {
      val updatedRobot = robot.moveTo(newPosition)
      val updatedEnv = env.updateActiveRobot(updatedRobot)
      TickResult.fromResult(
        updatedEnv ?: env,
        BState.Success,
      )
    }
  }

  class MoveToDir(
    val direction: Direction,
  ) : GridWorldANode {
    override val name = "moveTo$direction"

    override fun execute(env: GridWorld): GridWorldTickResult = move(env, direction)

    override fun toString(): String = name
  }

  class MoveForward : GridWorldANode {
    override val name = "moveForward"

    override fun execute(env: GridWorld): GridWorldTickResult =
      move(env, env.activeRobot().turnDirection())

    override fun toString(): String = name
  }
}
