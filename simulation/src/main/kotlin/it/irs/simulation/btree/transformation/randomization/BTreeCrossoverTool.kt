package it.irs.simulation.btree.transformation.randomization

import it.irs.simulation.Environment
import it.irs.simulation.SimulationUtils.DEFAULT_SEED
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.transformation.TransformationUtils.getNthNode
import it.irs.simulation.btree.transformation.TransformationUtils.replaceNthNode
import kotlin.random.Random

class BTreeCrossoverTool<E>(
  override val random: Random = Random(DEFAULT_SEED),
) : BinaryRandomTool<E> where E : Environment<*> {
  override val name = "crossover"

  override fun transform(
    btree1: BehaviorTree<E>,
    btree2: BehaviorTree<E>,
  ): Pair<BehaviorTree<E>, BehaviorTree<E>> =
    if (btree1.size <= 1 || btree2.size <= 1) {
      Pair(btree1, btree2)
    } else {
      val index1 = random.nextInt(1, btree1.size)
      val node1 = getNthNode(btree1.root, index1)

      val validIndices =
        (0 until btree2.size).filter { index ->
          val node2 = getNthNode(btree2.root, index)
          node1.javaClass == node2.javaClass
        }

      if (validIndices.isEmpty()) {
        Pair(btree1, btree2)
      } else {
        val index2 = validIndices[random.nextInt(validIndices.size)]

        val newTree1 =
          replaceNthNode(btree1.root, index1, { _, _ -> listOf(getNthNode(btree2.root, index2)) })
        val newTree2 = replaceNthNode(btree2.root, index2, { _, _ -> listOf(node1) })

        Pair(BehaviorTree(newTree1), BehaviorTree(newTree2))
      }
    }

  override fun toString(): String = name
}
