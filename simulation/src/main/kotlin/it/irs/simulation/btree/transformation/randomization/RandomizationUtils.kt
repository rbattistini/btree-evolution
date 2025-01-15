package it.irs.simulation.btree.transformation.randomization

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode
import it.irs.simulation.btree.node.branch.Selector
import it.irs.simulation.btree.node.branch.Sequence
import it.irs.simulation.btree.node.leaf.LeafNode
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import kotlin.random.Random

object RandomizationUtils {
  fun <E> randomNode(
    nodeRegistry: LeafNodeRegistry<E>,
    random: Random,
  ): TreeNode<E> where E : Environment<*> =
    if (random.nextBoolean()) {
      randomCompositeNode(random)
    } else {
      randomLeafNode(nodeRegistry, random)
    }

  fun <E> randomCompositeNode(random: Random): CompositeNode<E> where E : Environment<*> {
    val isSequence = random.nextBoolean()
    return if (isSequence) {
      Sequence()
    } else {
      Selector()
    }
  }

  fun <E> randomLeafNode(
    nodeRegistry: LeafNodeRegistry<E>,
    random: Random,
  ): LeafNode<E> where E : Environment<*> {
    val isCondition = random.nextBoolean()
    val r =
      if (isCondition) {
        nodeRegistry.conditionNodes()
      } else {
        nodeRegistry.actionNodes()
      }
    return r.values.random(random)
  }

  fun <E> CompositeNode<E>.getRandomCompositeNodeAtDepth(
    targetDepth: Int,
    random: Random,
  ): CompositeNode<E> where E : Environment<*> =
    if (targetDepth <= 1) {
      this
    } else {
      val compositeChildren =
        (this as? CompositeNode<E>)?.children?.filterIsInstance<CompositeNode<E>>() ?: emptyList()
      if (compositeChildren.isEmpty()) {
        this
      } else {
        val candidates =
          compositeChildren.map {
            it.getRandomCompositeNodeAtDepth(
              targetDepth - 1,
              random,
            )
          }

        candidates.random(random)
      }
    }

  fun <E> CompositeNode<E>.addNodeAtDepthRandomly(
    newNode: TreeNode<E>,
    targetDepth: Int,
    random: Random,
  ): CompositeNode<E> where E : Environment<*> =
    if (targetDepth <= 1) {
      val newChildren = children.toMutableList()
      newChildren.add(newNode)
      withChildren(newChildren)
    } else {
      val compositeChildren = children.filterIsInstance<CompositeNode<E>>()
      if (compositeChildren.isEmpty()) {
        this
      } else {
        val chosenChild = compositeChildren.random(random)

        val updatedChildren =
          children.map { child ->
            if (child == chosenChild) {
              (child as CompositeNode<E>).addNodeAtDepthRandomly(newNode, targetDepth - 1, random)
            } else {
              child
            }
          }
        withChildren(updatedChildren)
      }
    }
}
