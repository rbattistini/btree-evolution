package it.irs.simulation.btree.builder

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.Selector
import it.irs.simulation.btree.node.branch.Sequence

class BTreeBuilder<E : Environment<*>> {
  private val children = mutableListOf<TreeNode<E>>()

  operator fun TreeNode<E>.unaryPlus() {
    children.add(this)
  }

  fun seq(
    name: String = "Sequence",
    init: BTreeBuilder<E>.() -> Unit,
  ): Sequence<E> {
    val builder = BTreeBuilder<E>()
    builder.init()
    return Sequence(name, builder.children)
  }

  fun sel(
    name: String = "Selector",
    init: BTreeBuilder<E>.() -> Unit,
  ): Selector<E> {
    val builder = BTreeBuilder<E>()
    builder.init()
    return Selector(name, builder.children)
  }

  fun build(): List<TreeNode<E>> = children
}

fun <E : Environment<*>> btree(init: BTreeBuilder<E>.() -> Unit): BehaviorTree<E> {
  val builder = BTreeBuilder<E>()
  builder.init()
  return BehaviorTree(builder.build().first())
}
