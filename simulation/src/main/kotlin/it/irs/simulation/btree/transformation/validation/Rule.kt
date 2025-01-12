package it.irs.simulation.btree.transformation.validation

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.TreeNode

interface Rule {
  val name: String
    get() = this.javaClass.simpleName

  fun <E> check(node: TreeNode<E>): Boolean where E : Environment<*>
}
