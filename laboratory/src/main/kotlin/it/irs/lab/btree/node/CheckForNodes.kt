package it.irs.lab.btree.node

import it.irs.lab.blackboard.RobotWrapper.RobotGenericStorage.storePoints
import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.btree.GridWorldCNode
import it.irs.lab.btree.GridWorldTickResult
import it.irs.lab.btree.node.NodeUtils.resultOfRobotAction
import it.irs.lab.env.GridExtensions.lightCells
import it.irs.lab.env.GridExtensions.obstacles
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.result.TickResult
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.SquareGrid
import it.irs.simulation.space.grid.SquareGrid.Companion.DEFAULT_RADIUS
import java.awt.Color

object CheckForNodes {
  fun check(
    env: GridWorld,
    ent: Set<GridEntity>,
    radius: Int,
    dir: Set<Direction?> = Direction.entries.toSet(),
  ): Set<Point> {
    val robot = env.activeRobot()
    val positionsToCheck =
      ent
        .flatMap { gridEntity ->
          when (gridEntity) {
            GridEntity.RedLight ->
              SquareGrid.fromReferencePoint(
                robot.position(),
                env.space.lightCells(Color.RED),
                radius,
              )

            GridEntity.Visited -> TODO()

            GridEntity.GreenLight ->
              SquareGrid.fromReferencePoint(
                robot.position(),
                env.space.lightCells(Color.GREEN),
                radius,
              )

            GridEntity.Obstacle ->
              SquareGrid.fromReferencePoint(
                robot.position(),
                env.space.obstacles(),
                radius,
              )

            GridEntity.Boundary -> env.space.boundaries(robot.position(), radius)
          }
        }.toSet()

    val matches = robot.matchesInDirections(dir, positionsToCheck).orEmpty()
    return matches.mapNotNull { it }.toSet()
  }
}

class CheckForAndStore(
  private val entities: Set<GridEntity>,
) : GridWorldCNode {
  override val name = "checkForAndStore$entities"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val robot = env.activeRobot()
    val matches = CheckForNodes.check(env, entities, DEFAULT_RADIUS)
    return resultOfRobotAction(env, robot.storePoints("matches", matches))
  }

  override fun toString(): String = name
}

class CheckForDir(
  val entity: GridEntity,
  private val direction: Direction,
  private val radius: Int,
) : GridWorldCNode {
  override val name = "checkFor${entity.name}${direction.name}"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val matches =
      CheckForNodes.check(env, setOf(entity), radius, setOf(direction))
    return TickResult.fromResult(
      env,
      if (matches.isNotEmpty()) BState.Success else BState.Failure,
    )
  }

  override fun toString(): String = name
}

class CheckForDirSet(
  private val entities: Set<GridEntity>,
  private val direction: Direction,
  private val radius: Int,
) : GridWorldCNode {
  override val name = "checkFor${entities}${direction.name}"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val matches =
      CheckForNodes.check(env, entities, radius, setOf(direction))
    return TickResult.fromResult(
      env,
      if (matches.isNotEmpty()) BState.Success else BState.Failure,
    )
  }

  override fun toString(): String = name
}

class CheckFor(
  val entity: GridEntity,
  private val orientation: Orientation,
  private val radius: Int,
) : GridWorldCNode {
  override val name = "checkFor${entity.name}${orientation.name}"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val reference = env.activeRobot().position()
    val matches =
      CheckForNodes.check(env, setOf(entity), radius, setOf(orientation.toDirection(reference)))
    return TickResult.fromResult(
      env,
      if (matches.isNotEmpty()) BState.Success else BState.Failure,
    )
  }

  override fun toString(): String = name
}

class CheckForSet(
  private val entities: Set<GridEntity>,
  private val orientation: Orientation,
  private val radius: Int,
) : GridWorldCNode {
  override val name = "checkFor${entities}${orientation.name}"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val reference = env.activeRobot().position()
    val matches =
      CheckForNodes.check(env, entities, radius, setOf(orientation.toDirection(reference)))
    return TickResult.fromResult(
      env,
      if (matches.isNotEmpty()) BState.Success else BState.Failure,
    )
  }

  override fun toString(): String = name
}

class CheckForAll(
  val entity: GridEntity,
  private val radius: Int,
) : GridWorldCNode {
  override val name = "checkFor${entity.name}${Orientation.entries}"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val matches =
      CheckForNodes.check(env, setOf(entity), radius, Direction.entries.toSet())
    return TickResult.fromResult(
      env,
      if (matches.isNotEmpty()) BState.Success else BState.Failure,
    )
  }

  override fun toString(): String = name
}

class CheckForAllSet(
  private val entities: Set<GridEntity>,
  private val radius: Int,
) : GridWorldCNode {
  override val name = "checkFor${entities}${Orientation.entries}"

  override fun execute(env: GridWorld): GridWorldTickResult {
    val matches =
      CheckForNodes.check(env, entities, radius, Direction.entries.toSet())
    return TickResult.fromResult(
      env,
      if (matches.isNotEmpty()) BState.Success else BState.Failure,
    )
  }

  override fun toString(): String = name
}
