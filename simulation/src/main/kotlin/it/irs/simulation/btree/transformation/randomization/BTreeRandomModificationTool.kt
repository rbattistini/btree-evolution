package it.irs.simulation.btree.transformation.randomization

import it.irs.simulation.Environment
import it.irs.simulation.SimulationUtils.DEFAULT_SEED
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.TransformationUtils.replaceNthNode
import it.irs.simulation.btree.transformation.TransformationUtils.swapCompositeNode
import it.irs.simulation.btree.transformation.randomization.RandomizationUtils.randomLeafNode
import it.irs.simulation.btree.transformation.randomization.RandomizationUtils.randomNode
import kotlin.random.Random

class BTreeRandomModificationTool<E>(
  private val nodeRegistry: LeafNodeRegistry<E>,
  override val random: Random = Random(DEFAULT_SEED),
) : UnaryRandomTool<E> where E : Environment<*> {
  override val name = "randomModification"

  override fun transform(btree: BehaviorTree<E>): BehaviorTree<E> {
    val randomIdx = random.nextInt(btree.size)
    val nodeReplacer: (TreeNode<E>, Boolean) -> List<TreeNode<E>> = { node, isRoot ->
      when {
        isRoot ->
          listOf(
            if (node is CompositeNode) {
              swapCompositeNode(node)
            } else {
              randomNode(nodeRegistry, random)
            },
          )
        node is CompositeNode<E> ->
          if (random.nextBoolean()) {
            listOf(randomLeafNode(nodeRegistry, random)) + node.children
          } else {
            listOf(swapCompositeNode(node))
          }
        else -> listOf(randomNode(nodeRegistry, random))
      }
    }
    return BehaviorTree(replaceNthNode(btree.root, randomIdx, nodeReplacer))
  }

  override fun toString(): String = name
}
