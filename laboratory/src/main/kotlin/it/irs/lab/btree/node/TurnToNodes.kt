package it.irs.lab.btree.node

import it.irs.lab.blackboard.RobotWrapper.RobotGenericStorage.readPoints
import it.irs.lab.blackboard.RobotWrapper.RobotMovement.turnTo
import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.btree.GridWorldANode
import it.irs.lab.btree.GridWorldTickResult
import it.irs.lab.btree.node.NodeUtils.resultOfRobotAction
import it.irs.lab.env.GridWorld
import it.irs.lab.env.GridWorld.Companion.random
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.result.TickResult
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.Direction.Companion.getDirectionsFromPoints
import it.irs.simulation.space.grid.Direction.Companion.toDirection
import it.irs.simulation.space.grid.Orientation

class TurnToFollowStored : GridWorldANode {
  override val name = "turnToFollowStored"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val robot = env.activeRobot()
    val referencePoint = robot.position()
    val targetPoint = robot.readPoints("matches")?.first()
    val directionToTurn = targetPoint?.toDirection(referencePoint)
    return resultOfRobotAction(env, robot.turnTo(directionToTurn))
  }

  override fun toString(): String = name
}

class TurnToAvoidStored : GridWorldANode {
  override val name = "turnToAvoidStored"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val robot = env.activeRobot()
    val matches = robot.readPoints("matches")
    val position = robot.position()
    val directionsToAvoid = getDirectionsFromPoints(position, matches)

    if (directionsToAvoid.isNullOrEmpty()) return TickResult.fromResult(env, BState.Failure)

    val matchDirections =
      Direction.entries
        .toTypedArray()
        .filter { !directionsToAvoid.contains(it) }
    val directionToTurn = matchDirections.randomOrNull(random)

    return resultOfRobotAction(env, robot.turnTo(directionToTurn))
  }

  override fun toString(): String = name
}

class TurnToDir(
  private val direction: Direction,
) : GridWorldANode {
  override val name = "turnTo${direction.name}"

  override fun execute(env: GridWorld): GridWorldTickResult =
    resultOfRobotAction(env, env.activeRobot().turnTo(direction))

  override fun toString(): String = name
}

class TurnTo(
  private val orientation: Orientation,
) : GridWorldANode {
  override val name = "turnTo${orientation.name}"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val robotPosition = env.activeRobot().position()
    val dir = orientation.toDirection(robotPosition)
    return resultOfRobotAction(env, env.activeRobot().turnTo(dir))
  }

  override fun toString(): String = name
}

class TurnRandomly : GridWorldANode {
  override val name = "turnRandomly"

  override fun execute(env: GridWorld): GridWorldTickResult =
    resultOfRobotAction(
      env,
      env
        .activeRobot()
        .turnTo(Direction.entries.random(random)),
    )

  override fun toString(): String = name
}
