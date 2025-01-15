package it.irs.lab.btree.transformation.randomization

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.btree.node.NodeUtils.checkFor
import it.irs.lab.btree.node.NodeUtils.turnRandomly
import it.irs.lab.btree.node.NodeUtils.turnTo
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.lab.btree.transformation.randomization.BTRandomizationSpecHelper.registry
import it.irs.simulation.btree.builder.BTreeRandomGenerator
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation
import kotlin.random.Random

class BTRandomGenSpec :
  ShouldSpec({
    context("Testing random generation") {
      should("randomly generate a tree") {
        val rndTreeGen = BTreeRandomGenerator(registry, random = Random(42))
        val randomTree = rndTreeGen.randomTree()
        randomTree.string shouldBe
          btree {
            +sel {
              +seq {
                +sel {
                  +checkFor(GridEntity.Visited, Orientation.Forward)
                  +turnToFollowStored
                }
                +seq {
                  +turnTo(Orientation.Right)
                  +turnTo(Orientation.Left)
                  +checkFor(GridEntity.GreenLight, Orientation.Left)
                }
                +sel {
                  +checkFor(GridEntity.Obstacle, Orientation.Right)
                  +checkFor(GridEntity.Visited, Orientation.Left)
                }
              }
              +sel {
                +sel {
                  +turnTo(Orientation.Left)
                  +turnTo(Orientation.Right)
                  +checkFor(GridEntity.RedLight, Orientation.Left)
                }
                +sel {
                  +turnTo(Orientation.Left)
                  +turnRandomly
                  +checkFor(GridEntity.GreenLight, Orientation.Forward)
                }
              }
            }
          }.string
      }
    }
  })
