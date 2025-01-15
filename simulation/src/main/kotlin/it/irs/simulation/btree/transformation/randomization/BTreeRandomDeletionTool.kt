package it.irs.simulation.btree.transformation.randomization

import it.irs.simulation.Environment
import it.irs.simulation.SimulationUtils.DEFAULT_SEED
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.transformation.TransformationUtils.deleteNthNode
import kotlin.random.Random

class BTreeRandomDeletionTool<E>(
  override val random: Random = Random(DEFAULT_SEED),
) : UnaryRandomTool<E> where E : Environment<*> {
  override val name = "randomDeletion"

  override fun transform(btree: BehaviorTree<E>): BehaviorTree<E> {
    val randomIdx = if (btree.size > 1) random.nextInt(1, btree.size) else 0
    return BehaviorTree(deleteNthNode(btree.root, randomIdx))
  }

  override fun toString(): String = name
}
