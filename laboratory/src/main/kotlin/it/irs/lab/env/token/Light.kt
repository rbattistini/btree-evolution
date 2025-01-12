package it.irs.lab.env.token

import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.Token
import java.awt.Color

data class Light(
  override val p: Point,
  val c: Color,
) : Token
