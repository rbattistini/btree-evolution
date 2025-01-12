package it.irs.simulation.btree

import it.irs.simulation.Environment

interface PlanFollower<E> where E : Environment<*> {
  val btree: BehaviorTree<E>

  fun addBehaviorTree(btree: BehaviorTree<E>): PlanFollower<E>
}
