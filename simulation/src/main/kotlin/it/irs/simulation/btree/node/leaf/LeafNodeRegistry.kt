package it.irs.simulation.btree.node.leaf

import it.irs.simulation.Environment

data class LeafNodeRegistry<E>(
  val nodes: Map<String, LeafNode<E>>,
) where E : Environment<*> {
  companion object
}
