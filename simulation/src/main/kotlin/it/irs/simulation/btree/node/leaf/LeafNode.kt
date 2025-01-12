package it.irs.simulation.btree.node.leaf

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.result.TickResult
import it.irs.simulation.btree.node.result.TreeResult

interface LeafNode<E> : TreeNode<E> where E : Environment<*> {
  fun execute(env: E): TickResult<E>

  override val size: Int get() = 1

  override val depth: Int get() = 1

  override fun tick(env: E): TreeResult<E> {
    val res = execute(env)
    return TreeResult(this, res.state, res.env)
  }
}
