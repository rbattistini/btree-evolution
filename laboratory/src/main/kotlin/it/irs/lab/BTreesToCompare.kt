package it.irs.lab

import it.irs.lab.btree.node.NodeUtils.checkFor
import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.checkForSet
import it.irs.lab.btree.node.NodeUtils.isNearerLight
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnRandomly
import it.irs.lab.btree.node.NodeUtils.turnTo
import it.irs.lab.btree.node.NodeUtils.turnToAvoidStored
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.lab.btree.node.Stop
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.GridEntity.Boundary
import it.irs.simulation.space.grid.GridEntity.Obstacle
import it.irs.simulation.space.grid.GridEntity.Visited
import it.irs.simulation.space.grid.Orientation

object BTreesToCompare {
  val handcraftedBTree1 =
    btree {
      +sel("Navigation") {
        +seq("FollowGreenLight") {
          +checkForAndStore(setOf(GridEntity.GreenLight))
          +turnToFollowStored
          +moveForward
        }
        +seq {
          +sel {
            +seq {
              +checkForAndStore(setOf(Obstacle, Boundary, Visited))
              +turnToAvoidStored
            }
            +turnRandomly
          }
          +moveForward
        }
      }
    }
  val handcraftedBTree2 =
    btree {
      +seq {
        +sel {
          +isNearerLight
          +sel {
            +seq {
              +checkForAndStore(setOf(Obstacle, Boundary, Visited))
              +turnToAvoidStored
            }
            +turnRandomly
          }
        }
        +moveForward
      }
    }

  // obtained with montecarlo on bigger grids
  // worst fitness experiments converge to this
  val randomBTree =
    btree {
      +seq {
        +moveForward
        +turnRandomly
      }
    }

  // works well in a smaller grid
  // most high fitness experiments converge to this form
  val exp1BTree =
    btree {
      +seq {
        +checkForAndStore(setOf(Obstacle, Boundary, Visited))
        +turnToAvoidStored
        +moveForward
      }
    }
  val exp2BTree =
    btree {
      +seq {
        +turnRandomly
        +checkForAndStore(setOf(Obstacle, Boundary, Visited))
        +turnToAvoidStored
        +Stop()
        +moveForward
        +sel {
          +turnRandomly
        }
        +turnRandomly
        +sel {
          +turnToFollowStored
        }
      }
    } // same as exp1Btree if simplified

  val exp3BTree =
    btree {
      +moveForward
      +checkFor(GridEntity.GreenLight, Orientation.Forward)
      +turnRandomly
      +turnTo(Orientation.Left)
      +checkForSet(setOf(Obstacle, Boundary, Visited), Orientation.Right)
      +seq {
        +turnTo(Orientation.Left)
        +turnRandomly
      }
    } // same as random if simplified

  val exp4BTree =
    btree {
      +seq {
        +checkForAndStore(setOf(Obstacle, Boundary, Visited))
        +sel {
          +turnToAvoidStored
          +turnRandomly
        }
        +moveForward
      }
    }
}
