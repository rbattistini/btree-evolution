package it.irs.simulation.btree.node.leaf

import it.irs.simulation.Environment

data class LeafNodeRegistry<E>(
  val nodes: Map<String, LeafNode<E>>,
) where E : Environment<*> {
  fun conditionNodes(): Map<String, LeafNode<E>> = nodes.filter { it.value is ConditionNode<E> }

  fun actionNodes(): Map<String, LeafNode<E>> = nodes.filter { it.value is ActionNode<E> }

  companion object
}
