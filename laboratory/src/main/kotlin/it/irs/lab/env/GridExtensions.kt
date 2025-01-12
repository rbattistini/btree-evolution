package it.irs.lab.env

import it.irs.lab.env.cell.Obstacle
import it.irs.lab.env.token.Light
import it.irs.lab.env.token.Start
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.SquareGrid
import java.awt.Color

object GridExtensions {
  fun SquareGrid.obstacles(): Set<Point> =
    this.cells
      .filterIsInstance<Obstacle>()
      .map { it.p }
      .toSet()

  fun SquareGrid.lightCells(color: Color): Set<Point> =
    this.tokens
      .filterIsInstance<Light>()
      .filter { it.c == color }
      .map { it.p }
      .toSet()

  fun SquareGrid.lightCells(colors: Set<Color>): Set<Point> =
    this.tokens
      .filterIsInstance<Light>()
      .filter { it.c in colors }
      .map { it.p }
      .toSet()

  fun SquareGrid.startPositions(): Set<Point> =
    this.tokens
      .filterIsInstance<Start>()
      .map { it.p }
      .toSet()
}
