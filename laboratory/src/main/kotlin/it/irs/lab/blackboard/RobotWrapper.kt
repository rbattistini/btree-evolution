package it.irs.lab.blackboard

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import it.irs.lab.env.entity.Robot
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.Point

object RobotWrapper {
  private val logger: KLogger = KotlinLogging.logger {}

  object RobotStatistics {
    fun Robot.addIdleStep() = copy(bb = incrementByOne("idleSteps"))

    fun Robot.idleSteps() = bb.read("idleSteps") as Int?

    fun Robot.addRevisitedCellStep() = copy(bb = incrementByOne("revisitedCellSteps"))

    fun Robot.revisitingSteps() = bb.read("revisitedCellSteps") as Int?

    fun Robot.addCollisionStep() = copy(bb = incrementByOne("collisionSteps"))

    fun Robot.collisionSteps() = bb.read("collisionSteps") as Int?

    private fun Robot.incrementByOne(field: String): Blackboard {
      val f = bb.read(field) as Int?
      return if (f != null) {
        bb.write(field to f + 1)
      } else {
        bb.write(field to 1)
      }
    }
  }

  object RobotMovement {
    fun Robot.turnTo(d: Direction) = copy(bb = bb.write("turnDirection" to d))

    @JvmName("turnToNullable")
    fun Robot.turnTo(d: Direction?) =
      if (d != null) {
        logger.debug { "Turned to direction $d" }
        copy(bb = bb.write("turnDirection" to d))
      } else {
        logger.debug { "Could not turn" }
        null
      }

    fun Robot.moveTo(p: Point?) =
      if (p != null) {
        logger.debug { "Moved to position $p" }
        copy(bb = bb.write("position" to p))
      } else {
        logger.debug { "Could not move" }
        null
      }

    fun Robot.teleportTo(p: Point) = copy(bb = bb.write("position" to p))
  }

  object RobotGenericStorage {
    fun Robot.readPoints(pName: String): Set<Point>? {
      val matches = bb.read(pName) as Set<*>?
      return matches?.filterIsInstance<Point>()?.toSet()
    }

    fun Robot.updatePoint(
      pName: String,
      p: Point?,
    ) = if (p != null) {
      val set = bb.read(pName) as Set<*>?
      val pointSet = set?.filterIsInstance<Point>()?.toSet()

      logger.debug { "Updated point $p" }
      val newBb =
        if (pointSet != null) {
          bb.write(pName to pointSet.plus(p))
        } else {
          bb.write(pName to setOf(p))
        }
      copy(bb = newBb)
    } else {
      logger.debug { "Could not store the point" }
      null
    }

    fun Robot.storePoints(
      pName: String,
      p: Set<Point>?,
    ) = if (!p.isNullOrEmpty()) {
      logger.debug { "Stored points $p" }
      copy(bb = bb.write(pName to p))
    } else {
      logger.debug { "Could not store points" }
      null
    }
  }

  object RobotState {
    fun Robot.turnDirection() = bb.read("turnDirection") as Direction?

    fun Robot.position() = bb.read("position") as Point?

    fun Robot.markRobotAsIdle() = copy(bb = bb.write("isIdle" to true))

    fun Robot.markRobotAsMoving() = copy(bb = bb.write("isIdle" to false))

    fun Robot.isIdle() = bb.read("isIdle") as Boolean?
  }
}
