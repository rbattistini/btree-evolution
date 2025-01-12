package it.irs.simulation.btree.node.result

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.TreeNode

data class TreeResult<E>(
  val root: TreeNode<E>,
  override val state: BState,
  override val env: E,
  val children: List<TreeResult<E>>? = null,
) : TickResult<E> where E : Environment<*> {
  fun stackTrace(indent: Int = 0): Sequence<String> =
    sequence {
      yield("${"   ".repeat(indent)}|- ${root.name} #${state.name}")

      children?.forEach {
        yieldAll(it.stackTrace(indent + 1))
      }
    }
}
