package it.irs.simulation.btree.node.branch

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.result.TreeResult

/**
 * Sequences execute their child nodes in order until one fails or all succeed.
 * Similar to an AND operator.
 */
data class Sequence<E>(
  override val name: String = "Sequence",
  override val children: List<TreeNode<E>> = listOf(),
) : CompositeNode<E> where E : Environment<*> {
  override val successCondition: (TreeResult<E>) -> Boolean =
    { it.state == BState.Failure || it.state == BState.Abort }

  override fun tick(env: E): TreeResult<E> = tickChildren(env, BState.Success)

  override fun withChildren(newChildren: List<TreeNode<E>>): CompositeNode<E> =
    copy(children = newChildren)

  override fun encode(): String = "seq_${super.encode()}"
}
