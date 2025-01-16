package it.irs.simulation.btree.transformation.reparation

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.node.leaf.ConditionNode
import it.irs.simulation.btree.transformation.TransformationUtils.deleteNthNode
import it.irs.simulation.btree.transformation.validation.Rule

class NoIdenticalAdjacentConditions(
  val keepChildren: Boolean,
) : DestructiveRepairTool {
  override val name = "NoIdenticalAdjacentConditions"

  override fun <E> repair(
    btree: BehaviorTree<E>,
    rule: Rule,
  ): BehaviorTree<E> where E : Environment<*> {
    val idx = findIdenticalAdjacentConditions(btree.root)
    return if (idx != null) BehaviorTree(deleteNthNode(btree.root, idx, keepChildren)) else btree
  }

  private fun <E> findIdenticalAdjacentConditions(
    node: TreeNode<E>,
    currentLevel: Int = 0,
    currentIndex: Int = 0,
    conditionNodesAtLevel: MutableMap<Int, MutableSet<ConditionNode<E>>> = mutableMapOf(),
  ): Int? where E : Environment<*> =
    when (node) {
      is ConditionNode<E> -> {
        val conditionsAtThisLevel =
          conditionNodesAtLevel.getOrPut(
            currentLevel,
          ) { mutableSetOf() }

        if (conditionsAtThisLevel.any { it.javaClass == node.javaClass }) {
          currentIndex
        } else {
          conditionsAtThisLevel.add(node)
          null
        }
      }
      is CompositeNode<E> -> {
        var index = currentIndex + 1
        var result: Int? = currentIndex
        for (child in node.children) {
          result =
            findIdenticalAdjacentConditions(
              child,
              currentLevel + 1,
              index,
              conditionNodesAtLevel,
            )
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
