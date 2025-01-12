package it.irs.simulation

import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.blackboard.BlackboardStore
import it.irs.simulation.entity.Entity
import it.irs.simulation.entity.EntityStore
import it.irs.simulation.space.EnvironmentSpace
import it.irs.simulation.space.SpatiallyLocalized
import kotlin.reflect.KProperty

interface Environment<S> :
  SpatiallyLocalized<S>,
  EntityStore,
  BlackboardStore where S : EnvironmentSpace {
  override fun addEntity(
    entityId: String,
    entity: Entity,
  ): Environment<S>

  fun copy(
    bb: Blackboard = this.bb,
    space: S = this.space,
    ent: Map<String, Entity> = this.entities,
  ): Environment<S>
}

operator fun <S, V, V1 : V> Environment<S>.getValue(
  thisRef: Any?,
  property: KProperty<*>,
): V1 where S : EnvironmentSpace =
  @Suppress("UNCHECKED_CAST")
  (bb.read(property.name) as V1)
