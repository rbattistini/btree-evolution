package it.irs.lab.env.token

import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.Token

data class Start(
  override val p: Point,
) : Token
