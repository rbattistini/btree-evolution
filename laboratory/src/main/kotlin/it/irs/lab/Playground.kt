package it.irs.lab

import it.irs.lab.ExperimentConfig.DEFAULT_DIMENSION
import it.irs.lab.ExperimentConfig.DEFAULT_SEED
import it.irs.lab.Playground.getEnv
import it.irs.lab.btree.GridWorldLeafNodeRegistry.moveTo
import it.irs.lab.env.GridExtensions.startPositions
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot
import it.irs.lab.env.random.GridValidator
import it.irs.lab.env.random.RandomGridGenerator
import it.irs.lab.fsm.robotState.Idle
import it.irs.lab.sim.FitnessComputer
import it.irs.lab.sim.GridWorldSim
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.fsm.StateMachine
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.Orientation
import kotlin.random.Random

object Playground {
  fun getEnv(): GridWorld {
    val fakeRobotId = "bb8"
    val btree =
      btree {
        +sel {
          +seq {
            +moveTo(Orientation.Left)
            +moveTo(Orientation.Right)
          }
          +seq {
            +moveTo(Orientation.Left)
            +moveTo(Orientation.Right)
          }
        }
      }

    println(btree.string)

    val gridGenerator =
      RandomGridGenerator(
        DEFAULT_DIMENSION,
        numObstacles = 23,
        Random(DEFAULT_SEED),
      )

    val grid = GridValidator.genValidGrid(gridGenerator).getOrNull() ?: GridWorld.defaultGrid

    val robotBb =
      Blackboard
        .create()
        .write("position" to grid.startPositions().first())
        .write("turnDirection" to Direction.North)
    val robot = Robot(robotBb, btree)

    val bb =
      Blackboard
        .create()
        .write("activeRobotId" to fakeRobotId)

    return GridWorld(bb, grid, emptyMap()).addEntity(fakeRobotId, robot)
  }
}

fun main() {
  val env = getEnv()
  val fsm = StateMachine(Idle(env))
  val sim = GridWorldSim(fsm, maxSteps = 10, logResult = true)
  val ff = FitnessComputer()
  ff.evalSim0(sim)
}
