package it.irs.lab.btree.transformation.validation

import arrow.core.Either
import io.kotest.core.spec.style.ShouldSpec
import it.irs.lab.btree.GridWorldLeafNodeRegistry.checkForAndStore
import it.irs.lab.btree.GridWorldLeafNodeRegistry.moveForward
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnToAvoidStored
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnToFollowStored
import it.irs.lab.btree.transformation.TransformationSpecHelper.validate
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.transformation.validation.BTreeRules.conditionsNotLastChild
import it.irs.simulation.btree.transformation.validation.BTreeRules.controlNodeMustHaveChildren
import it.irs.simulation.btree.transformation.validation.BTreeRules.noConsecutiveControlNodes
import it.irs.simulation.btree.transformation.validation.BTreeRules.noIdenticalAdjacentConditions
import it.irs.simulation.space.grid.GridEntity

class BTValidationSpec :
  ShouldSpec({
    context("Testing validation") {

      should("validate successfully") {
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

        validate(btree, Either.Right(true))
      }

      should("fail due to a NoConsecutiveControlNodes rule violation") {
        val btree =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +seq("AvoidRedLightsObstaclesBoundaries") {
                  +moveForward
                }
              }
            }
          }

        validate(btree, Either.Left(noConsecutiveControlNodes))
      }

      should("fail due to a ControlNodeMustHaveChildren rule violation") {
        val btree =
          btree<GridWorld> {
            +sel("Navigation") {
              +seq("AvoidRedLightsObstaclesBoundaries") {}
            }
          }

        validate(btree, Either.Left(controlNodeMustHaveChildren))
      }

      should("fail due to a ConditionsNotLastChild rule violation") {
        val btree =
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
              }
            }
          }

        validate(btree, Either.Left(conditionsNotLastChild))
      }

      should("fail due to a NoIdenticalAdjacentConditions rule violation") {
        val btree =
          btree {
            +sel("Navigation") {
              +seq("AvoidRedLightsObstaclesBoundaries") {
                +checkForAndStore(
                  setOf(
                    GridEntity.RedLight,
                    GridEntity.Obstacle,
                  ),
                )
                +checkForAndStore(
                  setOf(
                    GridEntity.Boundary,
                  ),
                )
                +moveForward
              }
            }
          }

        validate(btree, Either.Left(noIdenticalAdjacentConditions))
      }
    }
  })
