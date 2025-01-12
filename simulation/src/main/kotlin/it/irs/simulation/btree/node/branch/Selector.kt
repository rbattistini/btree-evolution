package it.irs.simulation.btree.node.branch

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.result.TreeResult

/**
 * Selectors execute their child nodes in order until one succeeds or all fail.
 * Similar to an OR operator.
 *
 * If all children fail, it returns an instance of itself with state failure.
 * If a child has success, it returns an instance of itself with state success.
 */
data class Selector<E>(
  override val name: String = "Selector",
  override val children: List<TreeNode<E>> = mutableListOf(),
) : CompositeNode<E> where E : Environment<*> {
  override val successCondition: (TreeResult<E>) -> Boolean =
    { it.state == BState.Success || it.state == BState.Abort }

  override fun tick(env: E): TreeResult<E> = tickChildren(env, BState.Failure)

  override fun withChildren(newChildren: List<TreeNode<E>>): Selector<E> =
    copy(children = newChildren)

  override fun encode(): String = "sel_${super.encode()}"
}
