package it.irs.lab.btree.transformation.randomization

import arrow.core.Either
import io.kotest.core.spec.style.ShouldSpec
import it.irs.lab.btree.node.NodeUtils.checkFor
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnToAvoidStored
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.lab.btree.transformation.randomization.BTRandomizationSpecHelper.registry
import it.irs.lab.btree.transformation.randomization.BTRandomizationSpecHelper.transform
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.transformation.randomization.BTreeRandomModificationTool
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation
import kotlin.random.Random

class BTRandomModificationSpec :
  ShouldSpec({
    context("Testing random modification of a behavior tree") {

      should("mutate a composite node with a leaf node") {
        val rr = BTreeRandomModificationTool(registry, Random(45))
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
                +checkFor(GridEntity.RedLight, Orientation.Left)
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +turnToFollowStored
                +moveForward
                +moveForward
              }
            },
          ),
          rr,
        )
      }

      should("mutate a leaf node with a composite node") {
        val rr = BTreeRandomModificationTool(registry, Random(21))
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
                  +seq {}
                  +moveForward
                }
                +moveForward
              }
            },
          ),
          rr,
        )
      }

      should("mutate a leaf node with another leaf node") {
        val rr = BTreeRandomModificationTool(registry, Random(34))
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
                  +turnToAvoidStored
                  +turnToFollowStored
                  +moveForward
                }
                +moveForward
              }
            },
          ),
          rr,
        )
      }

      should("mutate the root composite node only with another composite node") {
        val rr = BTreeRandomModificationTool(registry, Random(4356))
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
              +seq {
                +seq("FollowGreenLight") {
                  +checkForAndStore(setOf(GridEntity.GreenLight))
                  +turnToFollowStored
                  +moveForward
                }
                +moveForward
              }
            },
          ),
          rr,
        )
      }

      should("mutate the root leaf node with another tree node") {
        val rr = BTreeRandomModificationTool(registry, Random(4356))
        val btree =
          btree {
            +moveForward
          }
        transform(
          btree,
          Either.Right(
            btree {
              +turnToFollowStored
            },
          ),
          rr,
        )
      }

      should("mutate a composite node with another composite node") {
        val rr = BTreeRandomModificationTool(registry, Random(234))
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
                +sel {
                  +checkForAndStore(setOf(GridEntity.GreenLight))
                  +turnToFollowStored
                  +moveForward
                }
                +moveForward
              }
            },
          ),
          rr,
        )
      }
    }
  })
