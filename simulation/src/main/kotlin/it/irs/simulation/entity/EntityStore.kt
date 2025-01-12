package it.irs.simulation.entity

interface EntityStore {
  val entities: Map<String, Entity>

  fun addEntity(
    entityId: String,
    entity: Entity,
  ): EntityStore
}
