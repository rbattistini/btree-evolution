package it.irs.simulation.btree.transformation.randomization

import it.irs.simulation.Environment
import it.irs.simulation.SimulationUtils.DEFAULT_SEED
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.randomization.RandomizationUtils.addNodeAtDepthRandomly
import it.irs.simulation.btree.transformation.randomization.RandomizationUtils.randomNode
import kotlin.random.Random

class BTreeRandomAdditionTool<E>(
  private val nodeRegistry: LeafNodeRegistry<E>,
  override val random: Random = Random(DEFAULT_SEED),
) : UnaryRandomTool<E> where E : Environment<*> {
  override val name = "randomAddition"

  override fun transform(btree: BehaviorTree<E>): BehaviorTree<E> {
    val randomDepth = random.nextInt(1, btree.depth)
    val randomNode = randomNode(nodeRegistry, random)
    val node =
      if (btree.root is CompositeNode<E>) {
        btree.root.addNodeAtDepthRandomly(randomNode, randomDepth, random)
      } else {
        btree.root
      }
    return BehaviorTree(node)
  }

  override fun toString(): String = name
}
