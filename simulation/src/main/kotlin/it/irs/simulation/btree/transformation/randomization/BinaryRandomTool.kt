package it.irs.simulation.btree.transformation.randomization

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree

interface BinaryRandomTool<E> : RandomTool where E : Environment<*> {
  fun transform(
    btree1: BehaviorTree<E>,
    btree2: BehaviorTree<E>,
  ): Pair<BehaviorTree<E>, BehaviorTree<E>>
}
