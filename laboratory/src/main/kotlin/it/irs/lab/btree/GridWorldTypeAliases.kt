package it.irs.lab.btree

import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.leaf.ActionNode
import it.irs.simulation.btree.node.leaf.ConditionNode
import it.irs.simulation.btree.node.leaf.LeafNode
import it.irs.simulation.btree.node.result.TickResult

typealias GridWorldBTree = BehaviorTree<GridWorld>
typealias GridWorldTickResult = TickResult<GridWorld>
typealias GridWorldLNode = LeafNode<GridWorld>
typealias GridWorldANode = ActionNode<GridWorld>
typealias GridWorldCNode = ConditionNode<GridWorld>
