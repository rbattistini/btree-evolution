package it.irs.lab.env.cell

import it.irs.simulation.space.grid.Cell
import it.irs.simulation.space.grid.Point

data class Obstacle(
  override val p: Point,
) : Cell
