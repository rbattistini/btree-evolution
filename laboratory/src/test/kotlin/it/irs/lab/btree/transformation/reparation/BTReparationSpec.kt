package it.irs.lab.btree.transformation.reparation

import arrow.core.Either
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.btree.GridWorldLeafNodeRegistry.checkFor
import it.irs.lab.btree.GridWorldLeafNodeRegistry.checkForAndStore
import it.irs.lab.btree.GridWorldLeafNodeRegistry.moveForward
import it.irs.lab.btree.GridWorldLeafNodeRegistry.moveToDir
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnTo
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnToAvoidStored
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnToFollowStored
import it.irs.lab.btree.transformation.TransformationSpecHelper.TEST_REPAIR_ATTEMPTS
import it.irs.lab.btree.transformation.TransformationSpecHelper.conRepair
import it.irs.lab.btree.transformation.TransformationSpecHelper.repair
import it.irs.lab.btree.transformation.TransformationSpecHelper.rules
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.transformation.validation.BTreeValidation.isValid
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation
import kotlin.test.DefaultAsserter.fail

class BTReparationSpec :
  ShouldSpec({
    context("Testing reparation") {
      should("have nothing to repair") {
        val btree =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +turnToFollowStored
                +moveForward
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

        repair(btree, Either.Right(btree))
      }

      should("try to repair the tree up to three times") {
        val brokenBt =
          btree {
            +sel {
              +seq {}
              +seq {
                +seq { +moveForward }
                +turnToAvoidStored
                +checkForAndStore(
                  setOf(
                    GridEntity.RedLight,
                    GridEntity.Obstacle,
                    GridEntity.Boundary,
                  ),
                )
              }
              +moveForward
            }
          }

        val expectedRepairedBt =
          btree {
            +sel {
              +seq { +moveToDir(Direction.West) }
              +seq {
                +moveForward
                +turnToAvoidStored
                +checkForAndStore(
                  setOf(
                    GridEntity.RedLight,
                    GridEntity.Obstacle,
                    GridEntity.Boundary,
                  ),
                )
                +moveToDir(Direction.West)
              }
              +moveForward
            }
          }

        val validationRes = brokenBt.isValid(rules)
        if (validationRes is Either.Left) {
          val repRes = conRepair.repairWithRetry(brokenBt, TEST_REPAIR_ATTEMPTS)
          println(repRes.string)
          println(expectedRepairedBt.string)
          repRes.string shouldBe expectedRepairedBt.string
        } else {
          fail("Could not test reparation")
        }
      }

      should("try to repair the tree up to three times") {
        val brokenBt =
          btree {
            +sel {
              +seq {
                +checkFor(GridEntity.GreenLight, Orientation.Forward)
                +checkFor(GridEntity.GreenLight, Orientation.Right)
              }
              +sel {
                +turnTo(Orientation.Left)
                +checkFor(GridEntity.GreenLight, Orientation.Forward)
              }
            }
          }

        val validationRes = brokenBt.isValid(rules)
        if (validationRes is Either.Left) {
          val repRes = conRepair.repairWithRetry(brokenBt, TEST_REPAIR_ATTEMPTS)
          println(repRes.string)
        } else {
          fail("Could not test reparation")
        }
      }
    }
  })
