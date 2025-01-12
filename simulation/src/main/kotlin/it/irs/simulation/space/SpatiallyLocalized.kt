package it.irs.simulation.space

interface SpatiallyLocalized<S> where S : EnvironmentSpace {
  val space: S
}
