package it.irs.simulation.entity

import it.irs.simulation.Environment
import it.irs.simulation.blackboard.BlackboardStore
import it.irs.simulation.btree.PlanFollower
import kotlin.reflect.KProperty

interface Agent<E> :
  Entity,
  BlackboardStore,
  PlanFollower<E> where E : Environment<*>

operator fun <E, V, V1 : V> Agent<E>.getValue(
  thisRef: Any?,
  property: KProperty<*>,
): V1 where E : Environment<*> =
  @Suppress("UNCHECKED_CAST")
  (bb.read(property.name) as V1)
