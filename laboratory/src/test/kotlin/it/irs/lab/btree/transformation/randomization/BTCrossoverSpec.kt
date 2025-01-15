package it.irs.lab.btree.transformation.randomization

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnRandomly
import it.irs.lab.btree.node.NodeUtils.turnToAvoidStored
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.transformation.randomization.BTreeCrossoverTool
import it.irs.simulation.space.grid.GridEntity

class BTCrossoverSpec :
  ShouldSpec({
    context("Testing crossover of a behavior tree") {
      should("perform a sub-tree swapping") {
        val crossover = BTreeCrossoverTool<GridWorld>()

        val bt1 =
          btree {
            +sel("Navigation") {
              +seq("FollowRedLight") {
                +checkForAndStore(setOf(GridEntity.RedLight))
                +turnToFollowStored
                +moveForward
              }
              +moveForward
            }
          }

        val bt2 =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +turnRandomly
              }
              +seq("AvoidRedLightsObstaclesBoundaries") {
                +checkForAndStore(
                  setOf(
                    GridEntity.RedLight,
                    GridEntity.Obstacle,
                    GridEntity.Boundary,
                  ),
                )
                +turnToAvoidStored
                +moveForward
              }
              +moveForward
            }
          }

        val expectedBt1 =
          btree {
            +sel("Navigation") {
              +seq("AvoidRedLightsObstaclesBoundaries") {
                +checkForAndStore(
                  setOf(
                    GridEntity.RedLight,
                    GridEntity.Obstacle,
                    GridEntity.Boundary,
                  ),
                )
                +turnToAvoidStored
                +moveForward
              }
              +moveForward
            }
          }

        val expectedBt2 =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +turnRandomly
              }
              +seq("FollowRedLight") {
                +checkForAndStore(setOf(GridEntity.RedLight))
                +turnToFollowStored
                +moveForward
              }
              +moveForward
            }
          }

        val (cbt1, cbt2) = crossover.transform(bt1, bt2)

        println(bt1.string)
        println(bt2.string)
        println(cbt1.string)
        println(cbt2.string)

        cbt1.string shouldBe expectedBt1.string
        cbt2.string shouldBe expectedBt2.string
      }
    }
  })
