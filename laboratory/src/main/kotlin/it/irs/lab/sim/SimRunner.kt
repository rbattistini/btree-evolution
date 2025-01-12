package it.irs.lab.sim

import it.irs.lab.blackboard.GridWorldWrapper.writeActiveRobotId
import it.irs.lab.blackboard.RobotWrapper.RobotMovement.teleportTo
import it.irs.lab.blackboard.RobotWrapper.RobotMovement.turnTo
import it.irs.lab.btree.GridWorldBTree
import it.irs.lab.env.GridExtensions.startPositions
import it.irs.lab.env.GridWorld
import it.irs.lab.env.GridWorld.Companion.random
import it.irs.lab.env.entity.Robot
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.space.grid.Direction

object SimRunner {
  fun getEnv(btree: GridWorldBTree): GridWorld {
    val fakeRobotId = "bb8"
//    val gridGenerator =
//      RandomGridGenerator(
//        DEFAULT_DIMENSION,
//        DEFAULT_NUM_OBSTACLES,
//        random,
//      )
// GridValidator.genValidGrid(gridGenerator).getOrNull() ?:
    val randomGrid = GridWorld.defaultGrid
    val startPosition = randomGrid.startPositions().random(random)

    val randomDirection = Direction.entries.random(random)
    val robot =
      Robot(btree)
        .turnTo(randomDirection)
        .teleportTo(startPosition)

    val bb =
      Blackboard
        .create()
        .writeActiveRobotId(fakeRobotId)

    val fakeEnv =
      GridWorld(
        bb = bb,
        entities = mapOf(fakeRobotId to robot),
        space = randomGrid,
      )
    return fakeEnv
  }
}
