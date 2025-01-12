package it.irs.simulation.btree.node.leaf

import it.irs.simulation.Environment

interface ConditionNode<E> : LeafNode<E> where E : Environment<*>
