package it.irs.simulation.btree.transformation.reparation

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.transformation.validation.Rule

interface DestructiveRepairTool : RepairTool {
  fun <E> repair(
    btree: BehaviorTree<E>,
    rule: Rule,
  ): BehaviorTree<E>where E : Environment<*>
}
