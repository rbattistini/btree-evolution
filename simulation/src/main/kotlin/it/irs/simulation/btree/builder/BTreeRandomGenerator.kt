package it.irs.simulation.btree.builder

import it.irs.simulation.Environment
import it.irs.simulation.SimulationUtils.DEFAULT_SEED
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.node.branch.Selector
import it.irs.simulation.btree.node.branch.Sequence
import it.irs.simulation.btree.node.leaf.LeafNode
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import kotlin.random.Random

class BTreeRandomGenerator<E>(
  private val registry: LeafNodeRegistry<E>,
  private val maxDepth: Int = DEFAULT_MAX_DEPTH,
  private val minChildren: Int = DEFAULT_MIN_CHILDREN,
  private val maxChildren: Int = DEFAULT_MAX_CHILDREN,
  val random: Random = Random(DEFAULT_SEED),
) where E : Environment<*> {
  fun randomTree(): BehaviorTree<E> = BehaviorTree(generateRandomTree())

  private fun generateRandomTree(currentDepth: Int = DEFAULT_START_DEPTH): TreeNode<E> {
    if (currentDepth == maxDepth) {
      return randomLeafNode()
    }

    return randomCompositeNodeWithChildren(
      currentDepth,
    )
  }

  private fun randomCompositeNodeWithChildren(currentDepth: Int): CompositeNode<E> {
    val compositeNode: CompositeNode<E> = randomCompositeNode(random)
    val numChildren = random.nextInt(minChildren, maxChildren)
    val children =
      List(numChildren) {
        generateRandomTree(currentDepth + 1)
      }
    return compositeNode.withChildren(children)
  }

  private fun randomLeafNode(): LeafNode<E> {
    val randomKey = registry.nodes.keys.random(random)
    return registry.nodes[randomKey] as LeafNode<E>
  }

  private fun randomCompositeNode(random: Random): CompositeNode<E> {
    val isSequence = random.nextBoolean()
    return if (isSequence) {
      Sequence()
    } else {
      Selector()
    }
  }

  companion object {
    private const val DEFAULT_START_DEPTH = 0
    private const val DEFAULT_MAX_DEPTH = 3
    private const val DEFAULT_MIN_CHILDREN = 2
    private const val DEFAULT_MAX_CHILDREN = 4
  }
}
