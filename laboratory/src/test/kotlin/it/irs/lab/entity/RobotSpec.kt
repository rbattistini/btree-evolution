package it.irs.lab.entity

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.entity.RobotSpecHelper.checkBatteryNode
import it.irs.lab.entity.RobotSpecHelper.checkObjectNearby
import it.irs.lab.entity.RobotSpecHelper.move
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.node.BState
import it.irs.simulation.space.grid.Direction.North
import it.irs.simulation.space.grid.Point

class RobotSpec :
  ShouldSpec({
    context("Robot Behavior Tests") {
      val fakeRobotId = "bb8"
      val lowBatteryLevel = 10
      val highBatteryLevel = 50
      val bb = Blackboard.create().write("activeRobotId" to fakeRobotId)
      val btree =
        btree {
          +seq {
            +checkBatteryNode
            +sel {
              +checkObjectNearby
              +move(North)
            }
          }
        }

      should("succeed when battery is sufficient and an object is nearby") {
        val robotBb =
          Blackboard
            .create()
            .write("batteryLevel" to highBatteryLevel)
            .write("isObjectNearby" to true)

        val robot = Robot(robotBb, btree)
        val fakeEnv = GridWorld(bb).addEntity("bb8", robot)
        btree.tick(fakeEnv).state shouldBe BState.Success
      }

      should("succeed when battery is sufficient but no object is nearby") {
        val robotBb =
          Blackboard
            .create()
            .write("position" to Point(0, 0))
            .write("batteryLevel" to highBatteryLevel)
            .write("isObjectNearby" to false)

        val robot = Robot(robotBb, btree)
        val fakeEnv = GridWorld(bb).addEntity("bb8", robot)
        btree.tick(fakeEnv).state shouldBe BState.Success
      }

      should("fail when battery is low and an object is nearby") {
        val robotBb =
          Blackboard
            .create()
            .write("batteryLevel" to lowBatteryLevel)
            .write("isObjectNearby" to true)

        val robot = Robot(robotBb, btree)
        val fakeEnv = GridWorld(bb).addEntity("bb8", robot)
        btree.tick(fakeEnv).state shouldBe BState.Failure
      }

      should("fail when battery is low and no object is nearby") {
        val robotBb =
          Blackboard
            .create()
            .write("batteryLevel" to lowBatteryLevel)
            .write("isObjectNearby" to false)

        val robot = Robot(robotBb, btree)
        val fakeEnv = GridWorld(bb).addEntity("bb8", robot)
        btree.tick(fakeEnv).state shouldBe BState.Failure
      }
    }
  })
