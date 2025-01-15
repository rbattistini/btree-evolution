package it.irs.lab.btree.transformation.randomization

import arrow.core.Either
import io.kotest.core.spec.style.ShouldSpec
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.lab.btree.transformation.randomization.BTRandomizationSpecHelper.transform
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.transformation.randomization.BTreeRandomDeletionTool
import it.irs.simulation.space.grid.GridEntity
import kotlin.random.Random

class BTRandomDeletionSpec :
  ShouldSpec({
    context("Testing the random deletion of a node from a behavior tree") {
      should("remove a random composite node from the tree") {
        val randomDeletion = BTreeRandomDeletionTool<GridWorld>(Random(23))
        val btree =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +turnToFollowStored
              }
              +moveForward
            }
          }

        transform(
          btree,
          Either.Right(
            btree {
              +sel("Navigation") {
                +turnToFollowStored
                +moveForward
              }
            },
          ),
          randomDeletion,
        )
      }

      should("remove a random leaf node from the tree") {
        val randomDeletion = BTreeRandomDeletionTool<GridWorld>(Random(123))
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
                  +turnToFollowStored
                  +moveForward
                }
                +moveForward
              }
            },
          ),
          randomDeletion,
        )
      }
    }
  })
