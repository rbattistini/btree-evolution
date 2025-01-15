package it.irs.lab

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.types.shouldBeTypeOf
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.isNearerLight
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnRandomly
import it.irs.lab.btree.node.NodeUtils.turnToAvoidStored
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot
import it.irs.lab.fsm.robotState.GoalReached
import it.irs.lab.fsm.robotState.Idle
import it.irs.lab.fsm.robotState.Moving
import it.irs.lab.sim.GridWorldSim
import it.irs.lab.sim.GridWorldSimStatistics
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.fsm.StateMachine
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.GridEntity.Boundary
import it.irs.simulation.space.grid.GridEntity.Obstacle
import it.irs.simulation.space.grid.GridEntity.Visited
import it.irs.simulation.space.grid.Point

class GridWorldSpec :
  ShouldSpec({
    fun getEnv(): GridWorld {
      val fakeRobotId = "bb8"
      val btree =
        btree {
          +seq {
            +sel {
              +isNearerLight
              +sel {
                +seq {
                  +checkForAndStore(setOf(Obstacle, Boundary, Visited))
                  +turnToAvoidStored
                }
                +turnRandomly
              }
            }
            +sel {
              +moveForward
              +seq {
                +checkForAndStore(setOf(Obstacle, Boundary, Visited))
                +turnToAvoidStored
                +moveForward
              }
            }
          }
        }
      println(btree.string)

      val robotBb =
        Blackboard
          .create()
          .write("position" to Point(0, 0))
          .write("turnDirection" to Direction.North)
      val robot = Robot(robotBb, btree)

      val bb =
        Blackboard
          .create()
          .write("activeRobotId" to fakeRobotId)
      val fakeEnv = GridWorld(bb).addEntity(fakeRobotId, robot)
      return fakeEnv
    }

    val env = getEnv()
    val fsm = StateMachine(Idle(env))
    val sim = GridWorldSim(fsm, maxSteps = 500, logResult = true)

    should("update the fsm correctly") {
      sim.fsm.currentState.shouldBeTypeOf<Idle>()
      val updatedSim = sim.tick(env)
      updatedSim.fsm.currentState.env
        .activeRobot()
        .btree
      updatedSim.fsm.currentState.shouldBeTypeOf<Moving>()
    }

    should("run until end correctly") {
      val terminatedSim = sim.execute()
      terminatedSim.fsm.currentState.shouldBeTypeOf<GoalReached>()
    }

    should("return a set of statistics") {
      val terminatedSim = sim.execute()
      val simStat = terminatedSim.finalStatistics() as GridWorldSimStatistics
      println(simStat)
      println(
        terminatedSim.fsm.currentState.env
          .activeRobot()
          .btree
          .string,
      )

      terminatedSim.fsm.currentState.shouldBeTypeOf<GoalReached>()
    }
  })
