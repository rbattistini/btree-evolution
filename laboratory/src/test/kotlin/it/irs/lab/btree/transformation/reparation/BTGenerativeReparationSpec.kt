package it.irs.lab.btree.transformation.reparation

import arrow.core.Either
import io.kotest.core.spec.style.ShouldSpec
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.lab.btree.transformation.TransformationSpecHelper.repair
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.space.grid.GridEntity

class BTGenerativeReparationSpec :
  ShouldSpec({
    context("Testing reparation") {
      should("repair a ControlNodeMustHaveChildren rule violation by addition") {
        val brokenBT =
          btree<GridWorld> {
            +sel("Navigation") {
              +seq("AvoidRedLightsObstaclesBoundaries") {}
            }
          }

        val repairedBT =
          btree {
            +sel("Navigation") {
              +seq("AvoidRedLightsObstaclesBoundaries") {
                +turnToFollowStored
              }
            }
          }

        repair(brokenBT, Either.Right(repairedBT))
      }

      should("repair a ConditionsNotLastChild rule violation by addition") {
        val brokenBT =
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

        val repairedBT =
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
                +turnToFollowStored
              }
            }
          }

        repair(brokenBT, Either.Right(repairedBT))
      }
    }
  })
