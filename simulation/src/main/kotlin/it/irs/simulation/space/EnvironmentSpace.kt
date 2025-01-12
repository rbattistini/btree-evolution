package it.irs.simulation.space

import it.irs.simulation.space.grid.Point

interface EnvironmentSpace {
  fun contains(p: Point): Boolean
}
