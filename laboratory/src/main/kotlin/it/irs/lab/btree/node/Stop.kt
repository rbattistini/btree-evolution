package it.irs.lab.btree.node

import it.irs.lab.btree.GridWorldANode
import it.irs.lab.btree.GridWorldTickResult
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.result.TickResult

class Stop : GridWorldANode {
  override val name = "Stop"

  override fun execute(env: GridWorld): GridWorldTickResult =
    TickResult.fromResult(env, BState.Success)

  override fun toString(): String = name
}
