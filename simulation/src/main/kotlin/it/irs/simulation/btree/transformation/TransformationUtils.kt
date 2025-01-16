package it.irs.simulation.btree.transformation

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.node.branch.Selector
import it.irs.simulation.btree.node.branch.Sequence

object TransformationUtils {
  fun <E> appendToNthCompositeNode(
    root: TreeNode<E>,
    targetIndex: Int,
    newNode: TreeNode<E>,
  ): TreeNode<E> where E : Environment<*> =
    when {
      targetIndex == 0 && root is CompositeNode<E> -> {
        root.withChildren(root.children + newNode)
      }
      root is CompositeNode<E> -> {
        var remaining = targetIndex - 1
        val newChildren =
          root.children.map { child ->
            val subtreeSize = child.size
            if (remaining < subtreeSize) {
              appendToNthCompositeNode(child, remaining, newNode)
            } else {
              remaining -= subtreeSize
              child
            }
          }
        root.withChildren(newChildren)
      }
      else -> root
    }

  fun <E> deleteNthNode(
    root: TreeNode<E>,
    targetIndex: Int,
    keepChildren: Boolean = false,
  ): TreeNode<E> where E : Environment<*> =
    when {
      targetIndex == 0 -> root
      root is CompositeNode<E> -> deleteFromCompositeNode(root, targetIndex, keepChildren)
      else -> root
    }

  private fun <E> deleteFromCompositeNode(
    node: CompositeNode<E>,
    targetIndex: Int,
    keepChildren: Boolean,
  ): TreeNode<E> where E : Environment<*> {
    var remaining = targetIndex - 1
    val newChildren = mutableListOf<TreeNode<E>>()

    for (child in node.children) {
      val subtreeSize = child.size
      if (remaining < subtreeSize) {
        val modifiedChild =
          when {
            remaining == 0 && keepChildren && child is CompositeNode<E> -> child.children
            remaining == 0 -> null
            else -> listOf(deleteNthNode(child, remaining, keepChildren))
          }

        modifiedChild?.let { newChildren.addAll(it) }
      } else {
        newChildren.add(child)
      }
      remaining -= subtreeSize
    }

    return node.withChildren(newChildren)
  }

  fun <E> replaceNthNode(
    root: TreeNode<E>,
    targetIndex: Int,
    nodeReplacer: (TreeNode<E>, Boolean) -> List<TreeNode<E>>,
  ): TreeNode<E> where E : Environment<*> =
    when {
      targetIndex == 0 -> {
        val isRoot = true
        nodeReplacer(root, isRoot).first()
      }
      root is CompositeNode<E> -> replaceFromCompositeNode(root, targetIndex, nodeReplacer)
      else -> root
    }

  private fun <E> replaceFromCompositeNode(
    root: CompositeNode<E>,
    targetIndex: Int,
    nodeReplacer: (TreeNode<E>, Boolean) -> List<TreeNode<E>>,
  ): TreeNode<E> where E : Environment<*> {
    var remaining = targetIndex - 1
    val newChildren = mutableListOf<TreeNode<E>>()

    for (child in root.children) {
      val subtreeSize = child.size
      if (remaining < subtreeSize) {
        val modifiedChild =
          if (remaining == 0) {
            nodeReplacer(child, false)
          } else {
            listOf(replaceNthNode(child, remaining, nodeReplacer))
          }
        newChildren.addAll(modifiedChild)
      } else {
        newChildren.add(child)
      }
      remaining -= subtreeSize
    }

    return root.withChildren(newChildren)
  }

  fun <E> swapCompositeNode(node: CompositeNode<E>): CompositeNode<E> where E : Environment<*> =
    when (node) {
      is Sequence<E> -> Selector(children = node.children)
      is Selector<E> -> Sequence(children = node.children)
      else -> node
    }

  fun <E> getNthNode(
    node: TreeNode<E>,
    n: Int,
  ): TreeNode<E> where E : Environment<*> =
    when {
      n == 0 -> node
      node is CompositeNode<E> -> {
        var result = node
        var remaining = n - 1
        for (child in node.children) {
          val subtreeSize = child.size
          if (remaining < subtreeSize) {
            result = getNthNode(child, remaining)
            break
          }
          remaining -= subtreeSize
        }
        result
      }
      else -> node
    }
}
