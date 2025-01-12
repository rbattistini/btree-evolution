package it.irs.lab.btree

import it.irs.lab.ExperimentConfig.DEFAULT_DIMENSION
import it.irs.lab.btree.node.CheckFor
import it.irs.lab.btree.node.CheckForAndStore
import it.irs.lab.btree.node.CheckForDir
import it.irs.lab.btree.node.CheckForDirSet
import it.irs.lab.btree.node.MoveForward
import it.irs.lab.btree.node.MoveTo
import it.irs.lab.btree.node.MoveToDir
import it.irs.lab.btree.node.TurnRandomly
import it.irs.lab.btree.node.TurnTo
import it.irs.lab.btree.node.TurnToAvoidStored
import it.irs.lab.btree.node.TurnToFollowStored
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation
import it.irs.simulation.space.grid.SquareGrid.Companion.DEFAULT_RADIUS

object GridWorldLeafNodeRegistry {
  val moveForward = MoveForward()
  val turnRandomly = TurnRandomly()
  val turnToAvoidStored = TurnToAvoidStored()
  val turnToFollowStored = TurnToFollowStored()
  val checkForAndStore: (Set<GridEntity>) -> GridWorldCNode = { ent -> CheckForAndStore(ent) }
  val turnTo: (Orientation) -> GridWorldANode = { orientation -> TurnTo(orientation) }
  val checkFor: (
    GridEntity,
    Orientation,
  ) -> GridWorldCNode = { gridEntity, orientation ->
    CheckFor(gridEntity, orientation, DEFAULT_RADIUS)
  }
  val moveToDir: (Direction) -> GridWorldANode = { dir -> MoveToDir(dir) }
  val moveTo: (Orientation) -> GridWorldANode = { o -> MoveTo(o) }

  fun LeafNodeRegistry.Companion.of() =
    LeafNodeRegistry(
      buildMap {
//        put("moveForward", moveForward)
//        put("turnRandomly", turnRandomly)
//        put("turnToAvoidStored", turnToAvoidStored)
//        put("turnToFollowStored", turnToFollowStored)
//        val node = CheckForAndStore(setOf(GridEntity.Obstacle, GridEntity.Boundary))
//        put(node.name, node)
//
//        put("moveRandomly", MoveRandomly())
//        Orientation.entries.forEach { o ->
//          val n1 = MoveTo(o)
//          put(n1.name, n1)
//        }

        Direction.entries.forEach { dir ->
//          val n = MoveToAvoid(dir)
//          put(n.name, n)
//          val n2 = TurnToDir(dir)
//          put(n2.name, n2)
          val n1 = MoveToDir(dir)
          put(n1.name, n1)
          val node =
            CheckForDirSet(
              setOf(GridEntity.Obstacle, GridEntity.Boundary),
              dir,
              DEFAULT_RADIUS,
            )
          put(node.name, node)

          val gg = CheckForDir(GridEntity.GreenLight, dir, DEFAULT_DIMENSION)
          put(gg.name, gg)
        }

//        setOf(Orientation.Left, Orientation.Right, Orientation.Forward).forEach { orientation ->
// //          if (orientation != Orientation.Forward) {
// //            val node = TurnTo(orientation)
// //            put(node.name, node)
// //          }
// //          else {
// //            put(
// //              "checkForGreenLightAhead${orientation.name}",
// //              CheckFor(GridEntity.GreenLight, orientation, GridWorld.DEFAULT_DIMENSION),
// //            )
// //          }
//          val node =
//            CheckForSet(
//              setOf(GridEntity.Obstacle, GridEntity.Boundary),
//              orientation,
//              DEFAULT_DIMENSION,
//            )
//          put(node.name, node)
//
//          val gg = CheckFor(GridEntity.GreenLight, orientation, DEFAULT_DIMENSION)
//          put(gg.name, gg)
//
// //          GridEntity.entries.forEach { entity ->
// //            val node = CheckFor(entity, orientation, DEFAULT_DIMENSION)
// //            put(node.name, node)
// //          }
//        }
      },
    )
}
