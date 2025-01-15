package it.irs.simulation.btree

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode

object BTreeView {
  fun <E> TreeNode<E>.string(
    prefix: String = "",
    isLast: Boolean = true,
  ): String where E : Environment<*> {
    val connector = if (isLast) "└── " else "├── "
    val childPrefix = if (isLast) "    " else "│   "

    val builder =
      if (this is CompositeNode) {
        val kind = this.javaClass.simpleName
        val (nameToDisplay, annotation) =
          if (name.isEmpty() || name == kind) {
            Pair(this.javaClass.simpleName, "")
          } else {
            Pair(name, " #$kind")
          }
        StringBuilder("$prefix$connector$nameToDisplay$annotation\n")
      } else {
        StringBuilder("$prefix$connector$name\n")
      }

    if (this is CompositeNode<E>) {
      children.forEachIndexed { index, child ->
        val childIsLast = index == children.size - 1
        builder.append(child.string(prefix + childPrefix, childIsLast))
      }
    }

    return builder.toString()
  }
}
