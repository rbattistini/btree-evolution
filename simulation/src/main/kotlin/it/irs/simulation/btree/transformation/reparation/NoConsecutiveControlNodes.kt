package it.irs.simulation.btree.transformation.reparation

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.transformation.TransformationUtils.deleteNthNode
import it.irs.simulation.btree.transformation.validation.Rule

class NoConsecutiveControlNodes(
  val keepChildren: Boolean,
) : DestructiveRepairTool {
  override val name = "NoConsecutiveControlNodes"

  override fun <E> repair(
    btree: BehaviorTree<E>,
    rule: Rule,
  ): BehaviorTree<E> where E : Environment<*> {
    val idx = findConsecutiveControlNodes(btree.root)
    return if (idx != null) BehaviorTree(deleteNthNode(btree.root, idx, keepChildren)) else btree
  }

  private fun <E> findConsecutiveControlNodes(
    node: TreeNode<E>,
    parentType: Class<CompositeNode<E>>? = null,
    currentIndex: Int = 0,
  ): Int? where E : Environment<*> =
    when {
      node is CompositeNode<E> && parentType != null && parentType == node.javaClass -> currentIndex
      node is CompositeNode<E> -> {
        var index = currentIndex + 1
        var result: Int? = currentIndex
        for (child in node.children) {
          result = findConsecutiveControlNodes(child, node.javaClass, index)
          if (result != null) {
            break
          }
          index += child.size
        }
        result
      }
      else -> null
    }

  override fun toString(): String = name
}
