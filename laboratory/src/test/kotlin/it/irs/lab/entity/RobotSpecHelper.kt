package it.irs.lab.entity

import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.btree.GridWorldBTree
import it.irs.lab.btree.GridWorldLNode
import it.irs.lab.btree.GridWorldTickResult
import it.irs.lab.env.GridExtensions.lightCells
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.result.TickResult
import it.irs.simulation.getValue
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.plus
import java.awt.Color

object RobotSpecHelper {
  tailrec fun repeatTicks(
    btree: GridWorldBTree,
    env: GridWorld,
    times: Int,
  ): GridWorldTickResult {
    if (times <= 0) return TickResult.fromResult(env, BState.Success)
    val result = btree.tick(env)
    return repeatTicks(btree, result.env, times - 1)
  }

  const val DEFAULT_TEST_SEED = 42

  val reactToLight =
    object : GridWorldLNode {
      override val name = "reactToLight"

      override fun execute(env: GridWorld): GridWorldTickResult {
        val position = env.activeRobot().position()
        val lightPositions = env.space.lightCells(setOf(Color.RED, Color.GREEN))
        println("Check light at $position")
        return if (position in lightPositions) {
          println("Found light")
          TickResult.fromResult(env, BState.Success)
        } else {
          println("No light found")
          TickResult.fromResult(env, BState.Failure)
        }
      }
    }

  val checkBatteryNode =
    object : GridWorldLNode {
      override fun execute(env: GridWorld): GridWorldTickResult {
        val batteryLevel = env.activeRobot().bb.read("batteryLevel") as Int?
        return if (batteryLevel != null && batteryLevel > 20) {
          TickResult.fromResult(env, BState.Success)
        } else {
          TickResult.fromResult(env, BState.Failure)
        }
      }
    }

  fun move(direction: Direction): GridWorldLNode {
    return object : GridWorldLNode {
      override val name = "move($direction)"

      override fun execute(env: GridWorld): GridWorldTickResult {
        val activeRobotId: String by env
        val robot = env.entities[activeRobotId] as Robot
        val grid = env.space
        val position = robot.bb.read("position") as Point?

        return if (position != null && grid.contains(position)) {
          val newPosition = position + direction.point
          val updatedRobot =
            robot.copy(bb = robot.bb.write("position" to newPosition))
          val updatedEnv = env.copy(ent = env.entities.plus(activeRobotId to updatedRobot))
          println("Moved to ${direction.name}")
          TickResult.fromResult(updatedEnv, BState.Success)
        } else {
          println("Cannot move ${direction.name}")
          TickResult.fromResult(env, BState.Failure)
        }
      }
    }
  }

  val checkObjectNearby =
    object : GridWorldLNode {
      override fun execute(env: GridWorld): GridWorldTickResult {
        val isObjectNearby = env.activeRobot().bb.read("isObjectNearby") as Boolean?
        return if (isObjectNearby != null && isObjectNearby) {
          println("Picking up object...")
          TickResult.fromResult(env, BState.Success)
        } else {
          println("No object to pick up.")
          TickResult.fromResult(env, BState.Failure)
        }
      }
    }
}
