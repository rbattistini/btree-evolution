package it.irs.lab

import it.irs.lab.btree.node.NodeUtils.checkForAndStore
import it.irs.lab.btree.node.NodeUtils.isNearerLight
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnRandomly
import it.irs.lab.btree.node.NodeUtils.turnToAvoidStored
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.simulation.btree.builder.btree
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.GridEntity.Boundary
import it.irs.simulation.space.grid.GridEntity.Obstacle
import it.irs.simulation.space.grid.GridEntity.Visited

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
  val randomBTree =
    btree {
      +seq {
        +moveForward
        +turnRandomly
      }
    }
//  val exp1BTree = btree {}
//  val exp2BTree = btree {}
//  val exp3BTree = btree {}
}
