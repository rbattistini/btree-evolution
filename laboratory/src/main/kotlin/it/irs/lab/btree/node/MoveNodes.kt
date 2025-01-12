package it.irs.lab.btree.node

import it.irs.lab.blackboard.RobotWrapper.RobotMovement.moveTo
import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.blackboard.RobotWrapper.RobotState.turnDirection
import it.irs.lab.btree.GridWorldANode
import it.irs.lab.btree.GridWorldTickResult
import it.irs.lab.btree.node.MoveNodes.move
import it.irs.lab.btree.node.NodeUtils.resultOfRobotAction
import it.irs.lab.env.GridWorld
import it.irs.lab.env.GridWorld.Companion.random
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.Orientation

object MoveNodes {
  fun move(
    env: GridWorld,
    turnDirection: Direction?,
  ): GridWorldTickResult {
    val robot = env.activeRobot()
    val newPosition = robot.move(turnDirection)
    return resultOfRobotAction(env, robot.moveTo(newPosition))
  }
}

class MoveTo(
  private val orientation: Orientation,
) : GridWorldANode {
  override val name = "moveTo$orientation"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val reference = env.activeRobot().position()
    return move(env, orientation.toDirection(reference))
  }

  override fun toString(): String = name
}

class MoveToDir(
  val direction: Direction,
) : GridWorldANode {
  override val name = "moveTo$direction"

  override fun execute(env: GridWorld): GridWorldTickResult = move(env, direction)

  override fun toString(): String = name
}

class MoveToAvoid(
  val direction: Direction,
) : GridWorldANode {
  override val name = "moveToAvoid$direction"

  override fun execute(env: GridWorld): GridWorldTickResult =
    move(
      env,
      direction.opposite(),
//      Direction.entries
//        .minus(direction)
// //        .minus(direction.opposite())
//        .random(random),
    )

  override fun toString(): String = name
}

class MoveRandomly : GridWorldANode {
  override val name = "moveRandomly"

  override fun execute(env: GridWorld): GridWorldTickResult =
    move(env, Direction.entries.random(random))

  override fun toString(): String = name
}

class MoveForward : GridWorldANode {
  override val name = "moveForward"

  override fun execute(env: GridWorld): GridWorldTickResult =
    move(env, env.activeRobot().turnDirection())

  override fun toString(): String = name
}
