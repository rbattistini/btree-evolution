package it.irs.simulation.btree.transformation.reparation

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.transformation.TransformationUtils.appendToNthCompositeNode
import it.irs.simulation.btree.transformation.validation.Rule

class ControlNodeMustHaveChildren : GenerativeRepairTool {
  override val name = "controlNodeMustHaveChildren"

  override fun <E> repair(
    btree: BehaviorTree<E>,
    rule: Rule,
    nodeFactory: () -> TreeNode<E>,
  ): BehaviorTree<E> where E : Environment<*> {
    val idx = findControlNodeWithNoChildren(btree.root)
    return if (idx !=
      null
    ) {
      BehaviorTree(appendToNthCompositeNode(btree.root, idx, nodeFactory()))
    } else {
      btree
    }
  }

  private fun <E> findControlNodeWithNoChildren(
    node: TreeNode<E>,
    currentIndex: Int = 0,
  ): Int? where E : Environment<*> =
    when {
      node is CompositeNode<E> && node.children.isEmpty() -> currentIndex
      node is CompositeNode<E> -> {
        var index = currentIndex + 1
        var result: Int? = currentIndex
        for (child in node.children) {
          result = findControlNodeWithNoChildren(child, index)
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
