package it.irs.simulation.btree.transformation.validation

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode

object BTreeRules {
  val controlNodeMustHaveChildren =
    object : Rule {
      override val name = "ControlNodeMustHaveChildren"

      override fun <E> check(node: TreeNode<E>): Boolean where E : Environment<*> =
        if (node is CompositeNode<E>) {
          node.children.isNotEmpty()
        } else {
          true
        }

      override fun toString(): String = name
    }

  val noConsecutiveControlNodes =
    object : Rule {
      override val name = "NoConsecutiveControlNodes"

      override fun <E> check(node: TreeNode<E>): Boolean where E : Environment<*> {
        if (node is CompositeNode<E>) {
          for (child in node.children) {
            if (child is CompositeNode<*> && child::class == node::class) {
              return false
            }
          }
        }
        return true
      }

      override fun toString(): String = name
    }

  val conditionsNotLastChild =
    object : Rule {
      override val name = "ConditionsNotLastChild"

      override fun <E> check(node: TreeNode<E>): Boolean where E : Environment<*> {
        if (node is CompositeNode<E>) {
          val lastChild = node.children.lastOrNull()
          if (lastChild != null && lastChild.isCondition()) {
            return false
          }
        }
        return true
      }

      override fun toString(): String = name
    }

  val noIdenticalAdjacentConditions =
    object : Rule {
      override val name = "NoIdenticalAdjacentConditions"

      override fun <E> check(node: TreeNode<E>): Boolean where E : Environment<*> {
        if (node is CompositeNode<E>) {
          for (i in 0 until node.children.size - 1) {
            val current = node.children[i]
            val next = node.children[i + 1]

            if (current.isCondition() && next.isCondition() && current::class == next::class) {
              return false
            }
          }
        }
        return true
      }

      override fun toString(): String = name
    }
}
