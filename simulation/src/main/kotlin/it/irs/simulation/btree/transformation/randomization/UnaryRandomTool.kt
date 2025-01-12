package it.irs.simulation.btree.transformation.randomization

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree

interface UnaryRandomTool<E> : RandomTool where E : Environment<*> {
  fun transform(btree: BehaviorTree<E>): BehaviorTree<E>
}
