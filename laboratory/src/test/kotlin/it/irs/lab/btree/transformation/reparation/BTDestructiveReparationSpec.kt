package it.irs.lab.btree.transformation.reparation

import arrow.core.Either
import io.kotest.core.spec.style.ShouldSpec
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.transformation.TransformationSpecHelper.repair
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.space.grid.GridEntity

class BTDestructiveReparationSpec :
  ShouldSpec({
    context("Testing reparation") {
      should("repair a NoConsecutiveControlNodes rule violation by subtraction") {
        val brokenBT =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +seq("AvoidRedLightsObstaclesBoundaries") {
                  +moveForward
                }
              }
            }
          }

        val repairedBT =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +moveForward
              }
            }
          }

        repair(brokenBT, Either.Right(repairedBT))
      }

      should("repair a NoIdenticalAdjacentConditions rule violation by subtraction") {
        val brokenBT =
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

        val repairedBT =
          btree {
            +sel("Navigation") {
              +seq("AvoidRedLightsObstaclesBoundaries") {
                +checkForAndStore(
                  setOf(
                    GridEntity.RedLight,
                    GridEntity.Obstacle,
                  ),
                )
                +moveForward
              }
            }
          }

        repair(brokenBT, Either.Right(repairedBT))
      }
    }
  })
