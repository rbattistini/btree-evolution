package it.irs.simulation.btree.node

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.leaf.ConditionNode
import it.irs.simulation.btree.node.result.TreeResult

interface TreeNode<E> where E : Environment<*> {
  val name: String
    get() = this.javaClass.simpleName

  val size: Int

  val depth: Int

  fun tick(env: E): TreeResult<E>

  fun encode() = name

  fun isCondition() = this is ConditionNode<E>
}
