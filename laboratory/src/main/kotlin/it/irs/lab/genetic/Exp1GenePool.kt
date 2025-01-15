package it.irs.lab.genetic

import it.irs.lab.btree.node.CheckNodes.CheckForAndStore
import it.irs.lab.btree.node.NodeUtils.isNearerLight
import it.irs.lab.btree.node.NodeUtils.moveForward
import it.irs.lab.btree.node.NodeUtils.turnToFollowStored
import it.irs.lab.btree.node.Stop
import it.irs.lab.btree.node.TurnNodes.TurnRandomly
import it.irs.lab.btree.node.TurnNodes.TurnToAvoidStored
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.space.grid.GridEntity
import kotlin.random.Random

object Exp1GenePool {
  fun LeafNodeRegistry.Companion.ofExp1(random: Random) =
    LeafNodeRegistry(
      buildMap {
        put("moveForward", moveForward)
        put("turnRandomly", TurnRandomly(random))
        put("turnToAvoidStored", TurnToAvoidStored(random))
        put("turnToFollowStored", turnToFollowStored)
        put("stop", Stop())

        put("isNearerLight", isNearerLight)

        val node =
          CheckForAndStore(setOf(GridEntity.Obstacle, GridEntity.Boundary, GridEntity.Visited))
        put(node.name, node)

        val node1 =
          CheckForAndStore(setOf(GridEntity.GreenLight))
        put(node1.name, node1)
      },
    )
}
