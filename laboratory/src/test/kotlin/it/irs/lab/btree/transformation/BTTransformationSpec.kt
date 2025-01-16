package it.irs.lab.btree.transformation

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.btree.GridWorldBTree
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnRandomly
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.btree.node.branch.Selector
import it.irs.simulation.btree.node.branch.Sequence
import it.irs.simulation.btree.transformation.TransformationUtils.appendToNthCompositeNode
import it.irs.simulation.btree.transformation.TransformationUtils.deleteNthNode
import it.irs.simulation.btree.transformation.TransformationUtils.replaceNthNode
import it.irs.simulation.space.grid.GridEntity

class BTTransformationSpec :
  ShouldSpec({
    context("Testing transformations of a behavior tree") {
      fun checkTransformation(
        btree: GridWorldBTree,
        expectedBTree: GridWorldBTree,
      ) {
        println(btree.string)
        println(expectedBTree.string)
        btree.string shouldBe expectedBTree.string
      }

      should("add a leaf node to the tree") {
        val targetIdx = 1
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
        val expectedBt =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +turnToFollowStored
                +moveForward
                +turnRandomly
              }
              +moveForward
            }
          }

        val transformedBt = appendToNthCompositeNode(btree.root, targetIdx, turnRandomly)
        checkTransformation(expectedBt, BehaviorTree(transformedBt))
      }

      should("add a composite node to the tree") {
        val targetIdx = 1
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
        val expectedBt =
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
          }

        val transformedBt = appendToNthCompositeNode(btree.root, targetIdx, Selector())
        checkTransformation(expectedBt, BehaviorTree(transformedBt))
      }

      should("remove a leaf node from the tree") {
        val targetIdx = 3
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
        val expectedBt =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +moveForward
              }
              +moveForward
            }
          }

        val transformedBt = deleteNthNode(btree.root, targetIdx)
        checkTransformation(expectedBt, BehaviorTree(transformedBt))
      }

      should("remove a composite node and its children from the tree") {
        val targetIdx = 1
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
        val expectedBt =
          btree {
            +sel("Navigation") {
              +moveForward
            }
          }

        val transformedBt = deleteNthNode(btree.root, targetIdx, keepChildren = false)
        checkTransformation(expectedBt, BehaviorTree(transformedBt))
      }

      should("remove a composite node from the tree preserving its children") {
        val targetIdx = 1
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
        val expectedBt =
          btree {
            +sel("Navigation") {
              +checkForAndStore(setOf(GridEntity.GreenLight))
              +turnToFollowStored
              +moveForward
              +moveForward
            }
          }

        val transformedBt = deleteNthNode(btree.root, targetIdx, keepChildren = true)
        checkTransformation(expectedBt, BehaviorTree(transformedBt))
      }

      should("swap a composite node with a leaf node") {
        val targetIdx = 1
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
        val expectedBt =
          btree {
            +sel("Navigation") {
              +turnRandomly
              +moveForward
            }
          }

        val transformedBt = replaceNthNode(btree.root, targetIdx) { _, _ -> listOf(turnRandomly) }
        checkTransformation(expectedBt, BehaviorTree(transformedBt))
      }

      should("swap a leaf node with a composite node") {
        val targetIdx = 5
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
        val expectedBt =
          btree {
            +sel("Navigation") {
              +seq("FollowGreenLight") {
                +checkForAndStore(setOf(GridEntity.GreenLight))
                +turnToFollowStored
                +moveForward
              }
              +seq {}
            }
          }

        val transformedBt = replaceNthNode(btree.root, targetIdx) { _, _ -> listOf(Sequence()) }
        checkTransformation(expectedBt, BehaviorTree(transformedBt))
      }
    }
  })
