package it.irs.simulation.btree.transformation.reparation

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.node.leaf.ConditionNode
import it.irs.simulation.btree.transformation.TransformationUtils.appendToNthCompositeNode
import it.irs.simulation.btree.transformation.validation.Rule

class ConditionNotLastChild : GenerativeRepairTool {
  override val name = "conditionsNotLastChild"

  override fun <E> repair(
    btree: BehaviorTree<E>,
    rule: Rule,
    nodeFactory: () -> TreeNode<E>,
  ): BehaviorTree<E> where E : Environment<*> {
    val idx = findParentOfConditionNodeAsLastChild(btree.root)
    return if (idx != null) {
      BehaviorTree(appendToNthCompositeNode(btree.root, idx, nodeFactory()))
    } else {
      btree
    }
  }

  private fun <E> findParentOfConditionNodeAsLastChild(
    node: TreeNode<E>,
    currentIndex: Int = 0,
  ): Int? where E : Environment<*> =
    if (node is CompositeNode<E>) {
      var index = currentIndex + 1
      var result: Int? = currentIndex
      for (i in node.children.indices) {
        val child = node.children[i]
        result = findParentOfConditionNodeAsLastChild(child, index)
        if (result != null) {
          break
        }
        index += child.size
      }
      result
        ?: if (node.children.isNotEmpty() && node.children.last() is ConditionNode<E>) {
          currentIndex
        } else {
          null
        }
    } else {
      null
    }

  override fun toString(): String = name
}
