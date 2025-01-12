package it.irs.lab.entity

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.entity.RobotSpecHelper.move
import it.irs.lab.entity.RobotSpecHelper.reactToLight
import it.irs.lab.entity.RobotSpecHelper.repeatTicks
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot
import it.irs.lab.env.token.Light
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.node.BState
import it.irs.simulation.space.grid.Direction.North
import it.irs.simulation.space.grid.Point
import java.awt.Color

class RobotWithLightSpec :
  ShouldSpec({
    context("Robot Behavior Tests") {
      val fakeRobotId = "bb8"
      val bb =
        Blackboard
          .create()
          .write("activeRobotId" to fakeRobotId)

      val btree =
        btree {
          +sel("Navigation") {
            +reactToLight
            +move(North)
          }
        }

      val robotBb =
        Blackboard
          .create()
          .write("position" to Point(0, 0))
      val robot = Robot(robotBb, btree)
      val fakeEnv = GridWorld(bb).addEntity(fakeRobotId, robot)
      val space = fakeEnv.space
      val fakeEnvWithLight =
        fakeEnv.copy(
          space =
            space.copy(
              tokens =
                space.tokens.plus(
                  Light(
                    p = Point(0, 1),
                    c = Color.RED,
                  ),
                ),
            ),
        )

      println(btree.root.encode())

      should("react to light when on FakeCellB") {
        val res = btree.tick(fakeEnv)
        res.state shouldBe BState.Success
        val position = res.env.activeRobot().position()
        position shouldBe Point(0, 1)
      }

      should("move to next cell when not on FakeCellB") {
        val res = repeatTicks(btree, fakeEnvWithLight, times = 2)
        res.state shouldBe BState.Success
        val position = res.env.activeRobot().position()
        position shouldBe Point(0, 1)
      }
    }
  })
