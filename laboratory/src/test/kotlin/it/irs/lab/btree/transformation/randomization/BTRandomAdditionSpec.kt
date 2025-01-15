package it.irs.lab.btree.transformation.randomization

import arrow.core.Either
import io.kotest.core.spec.style.ShouldSpec
import it.irs.lab.btree.node.NodeUtils.checkFor
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.lab.btree.transformation.randomization.BTRandomizationSpecHelper.registry
import it.irs.lab.btree.transformation.randomization.BTRandomizationSpecHelper.transform
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.transformation.randomization.BTreeRandomAdditionTool
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation
import kotlin.random.Random

class BTRandomAdditionSpec :
  ShouldSpec({
    context("Testing random alteration of a behavior tree") {
      should("add a random composite node to the tree") {
        val randomAddition = BTreeRandomAdditionTool(registry)
        val btree =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +turnToFollowStored
                +moveForward
              }
              +moveForward
            }
          }

        transform(
          btree,
          Either.Right(
            btree {
              +sel("Navigation") {
                +seq("FollowGreenLight") {
                  +checkForAndStore(setOf(GridEntity.GreenLight))
                  +turnToFollowStored
                  +moveForward
                  +sel {}
                }
                +moveForward
              }
            },
          ),
          randomAddition,
        )
      }

      should("add a random leaf node to the tree") {
        val randomAddition = BTreeRandomAdditionTool(registry, Random(123))
        val btree =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +turnToFollowStored
                +moveForward
              }
              +moveForward
            }
          }

        transform(
          btree,
          Either.Right(
            btree {
              +sel("Navigation") {
                +seq("FollowGreenLight") {
                  +checkForAndStore(setOf(GridEntity.GreenLight))
                  +turnToFollowStored
                  +moveForward
                }
                +moveForward
                +checkFor(GridEntity.GreenLight, Orientation.Right)
              }
            },
          ),
          randomAddition,
        )
      }
    }
  })
