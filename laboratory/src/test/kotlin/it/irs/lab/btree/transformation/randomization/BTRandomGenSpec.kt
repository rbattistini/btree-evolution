package it.irs.lab.btree.transformation.randomization

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.btree.GridWorldLeafNodeRegistry.checkFor
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnRandomly
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnTo
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
                  +checkFor(GridEntity.Obstacle, Orientation.Forward)
                  +checkFor(GridEntity.Boundary, Orientation.Left)
                }
                +sel {
                  +checkFor(GridEntity.GreenLight, Orientation.Forward)
                  +turnRandomly
                }
                +sel {
                  +checkFor(GridEntity.Boundary, Orientation.Left)
                  +turnTo(Orientation.Right)
                  +checkFor(GridEntity.Boundary, Orientation.Forward)
                }
              }
              +sel {
                +sel {
                  +checkFor(GridEntity.RedLight, Orientation.Forward)
                  +checkFor(GridEntity.Boundary, Orientation.Right)
                  +checkFor(GridEntity.Obstacle, Orientation.Right)
                }
                +sel {
                  +checkFor(GridEntity.RedLight, Orientation.Forward)
                  +checkFor(GridEntity.GreenLight, Orientation.Right)
                }
              }
            }
          }.string
      }
    }
  })
