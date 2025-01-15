package it.irs.lab.genetic

import it.irs.lab.btree.node.NodeUtils.checkForSet
import it.irs.lab.btree.node.NodeUtils.isNearerLight
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnTo
import it.irs.lab.btree.node.Stop
import it.irs.lab.btree.node.TurnNodes.TurnRandomly
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation.Forward
import it.irs.simulation.space.grid.Orientation.Left
import it.irs.simulation.space.grid.Orientation.Right
import kotlin.random.Random

object Exp2GenePool {
  fun LeafNodeRegistry.Companion.ofExp2(random: Random) =
    LeafNodeRegistry(
      buildMap {
        put("moveForward", moveForward)
        put("turnRandomly", TurnRandomly(random))
        put("stop", Stop())

        put("isNearerLight", isNearerLight)

        setOf(Forward, Left, Right).forEach { o ->
          if (o != Forward) {
            val node1 = turnTo(o)
            put(node1.name, node1)
          }

          val node2 =
            checkForSet(setOf(GridEntity.Obstacle, GridEntity.Boundary, GridEntity.Visited), o)
          put(node2.name, node2)

          val node3 =
            checkForSet(setOf(GridEntity.GreenLight), o)
          put(node3.name, node3)
        }
      },
    )
}
