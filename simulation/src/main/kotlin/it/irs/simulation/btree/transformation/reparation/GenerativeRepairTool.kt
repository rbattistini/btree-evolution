package it.irs.simulation.btree.transformation.reparation

import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.transformation.validation.Rule

interface GenerativeRepairTool : RepairTool {
  fun <E> repair(
    btree: BehaviorTree<E>,
    rule: Rule,
    nodeFactory: () -> TreeNode<E>,
  ): BehaviorTree<E> where E : Environment<*>
}
