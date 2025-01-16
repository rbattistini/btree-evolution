package it.irs.lab.sim

import it.irs.lab.blackboard.GridWorldWrapper.writeActiveRobotId
import it.irs.lab.blackboard.RobotWrapper.RobotMovement.teleportTo
import it.irs.lab.blackboard.RobotWrapper.RobotMovement.turnTo
import it.irs.lab.btree.GridWorldBTree
import it.irs.lab.env.GridExtensions.startPositions
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot
import it.irs.lab.env.random.GridValidator
import it.irs.lab.env.random.RandomGridGenerator
import it.irs.lab.experiment.config.ExperimentConfig
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.space.grid.Direction
import kotlin.random.Random

object SimRunner {
  fun getEnv(
    btree: GridWorldBTree,
    random: Random,
    cfg: ExperimentConfig,
  ): GridWorld {
    val fakeRobotId = "bb8"
    val gridGenerator =
      RandomGridGenerator(
        cfg.gridDimensions,
        cfg.gridObstacles,
      )

    val gridValidator =
      GridValidator(cfg.maxGridValidationAttempts, cfg.maxNeighbourRadiusForGridValidation)
    val randomGrid =
      gridValidator.genValidGrid(gridGenerator, random).getOrNull() ?: GridWorld.defaultGrid
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

    return GridWorld(
      bb = bb,
      entities = mapOf(fakeRobotId to robot),
      space = randomGrid,
    )
  }
}
