package it.irs.simulation.btree.node

import it.irs.simulation.Environment
import it.irs.simulation.SimulationUtils.DEFAULT_SEED
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.node.leaf.ActionNode
import it.irs.simulation.btree.node.leaf.ConditionNode
import it.irs.simulation.btree.node.result.TreeResult
import kotlin.math.abs
import kotlin.random.Random

interface TreeNode<E> where E : Environment<*> {
  val name: String
    get() = this.javaClass.simpleName

  val fullName: String
    get() = "$name-${abs(Random(DEFAULT_SEED).nextInt())}"

  val size: Int

  val depth: Int

  fun tick(env: E): TreeResult<E>

  fun encode() = name

  fun isComposite() = this is CompositeNode<E>

  fun isCondition() = this is ConditionNode<E>

  fun isAction() = this is ActionNode<E>
}
