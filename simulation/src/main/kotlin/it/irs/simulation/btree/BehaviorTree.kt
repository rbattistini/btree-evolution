package it.irs.simulation.btree

import it.irs.simulation.Environment
import it.irs.simulation.btree.BTreeView.string
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.result.TreeResult

data class BehaviorTree<E>(
  val root: TreeNode<E>,
) where E : Environment<*> {
  fun tick(env: E): TreeResult<E> = root.tick(env)

  val size: Int = root.size

  val depth: Int = root.depth

  val string: String = root.string()

  override fun toString(): String = root.toString()
}
