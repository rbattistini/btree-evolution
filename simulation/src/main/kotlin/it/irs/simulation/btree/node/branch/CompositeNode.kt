package it.irs.simulation.btree.node.branch

import it.irs.simulation.Environment
import it.irs.simulation.btree.builder.BTreeDslMarker
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.result.TreeResult

/**
 * Represents a branch with multiple children.
 *
 * Creates a more complex behavior by combining simpler behaviors.
 */
@BTreeDslMarker
interface CompositeNode<E> : TreeNode<E> where E : Environment<*> {
  val children: List<TreeNode<E>>
  val successCondition: (TreeResult<E>) -> Boolean

  override val size: Int
    get() = 1 + children.sumOf { it.size }

  override val depth: Int
    get() = 1 + (children.maxOfOrNull { it.depth } ?: 0)

  fun tickChildren(
    env: E,
    otherwise: BState,
  ): TreeResult<E> {
    val results = mutableListOf<TreeResult<E>>()
    for (child in children) {
      val result =
        if (results.isNotEmpty()) {
          child.tick(results.last().env)
        } else {
          child.tick(env)
        }
      results.add(result)
      if (successCondition(result)) {
        return TreeResult(this, result.state, result.env, results)
      }
    }

    // handle the case when there are no children
    return if (results.size > 0) {
      TreeResult(this, otherwise, results.last().env, results)
    } else {
      TreeResult(this, otherwise, env, results)
    }
  }

  fun withChildren(newChildren: List<TreeNode<E>>): CompositeNode<E>

  override fun encode(): String {
    val childStrings = children.joinToString(" ") { it.encode() }
    return "$name($childStrings)"
  }
}
