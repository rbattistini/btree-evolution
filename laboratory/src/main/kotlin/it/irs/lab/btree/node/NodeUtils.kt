package it.irs.lab.btree.node

import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.btree.GridWorldANode
import it.irs.lab.btree.GridWorldCNode
import it.irs.lab.btree.GridWorldTickResult
import it.irs.lab.btree.node.CheckNodes.CheckFor
import it.irs.lab.btree.node.CheckNodes.CheckForAndStore
import it.irs.lab.btree.node.CheckNodes.IsNearerLight
import it.irs.lab.btree.node.MoveNodes.MoveForward
import it.irs.lab.btree.node.TurnNodes.TurnRandomly
import it.irs.lab.btree.node.TurnNodes.TurnTo
import it.irs.lab.btree.node.TurnNodes.TurnToAvoidStored
import it.irs.lab.btree.node.TurnNodes.TurnToFollowStored
import it.irs.lab.env.GridExtensions.obstacles
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot
import it.irs.lab.experiment.config.DefaultConfig.defaultRandom
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.result.TickResult
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.SquareGrid.Companion.DEFAULT_RADIUS

object NodeUtils {
  val moveForward = MoveForward()
  val turnRandomly = TurnRandomly(defaultRandom)
  val turnToAvoidStored = TurnToAvoidStored(defaultRandom)
  val turnToFollowStored = TurnToFollowStored()
  val isNearerLight = IsNearerLight()
  val checkForAndStore: (Set<GridEntity>) -> GridWorldCNode = { ent -> CheckForAndStore(ent) }
  val turnTo: (Orientation) -> GridWorldANode = { orientation -> TurnTo(orientation) }
  val checkFor: (
    GridEntity,
    Orientation,
  ) -> GridWorldCNode = { gridEntity, orientation ->
    CheckFor(setOf(gridEntity), orientation, DEFAULT_RADIUS)
  }
  val checkForSet: (
    Set<GridEntity>,
    Orientation,
  ) -> GridWorldCNode = { gridEntity, orientation ->
    CheckFor(gridEntity, orientation, DEFAULT_RADIUS)
  }

  fun resultOfRobotAction(
    env: GridWorld,
    updatedRobot: Robot?,
  ): GridWorldTickResult {
    val updatedEnv = env.updateActiveRobot(updatedRobot)
    return TickResult.fromResult(
      updatedEnv ?: env,
      if (updatedEnv != null) BState.Success else BState.Failure,
    )
  }

  fun isCollision(
    currentRobotPosition: Point?,
    env: GridWorld,
  ): Boolean {
    val otherRobotPositions =
      env.entities.values
        .filterIsInstance<Robot>()
        .map { it.position() }
        .filter { it != currentRobotPosition }
        .toSet()
    val obstacles = env.space.obstacles()
    val boundaries = env.space.boundaries(currentRobotPosition)

    return otherRobotPositions.contains(currentRobotPosition) ||
      obstacles.contains(currentRobotPosition) ||
      boundaries.contains(currentRobotPosition)
  }
}
