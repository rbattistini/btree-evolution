package it.irs.lab.env.random

import it.irs.lab.env.GridWorld.Companion.lightColorToFollow
import it.irs.lab.env.cell.Clear
import it.irs.lab.env.cell.Obstacle
import it.irs.lab.env.token.Light
import it.irs.lab.env.token.Start
import it.irs.simulation.space.grid.Cell
import it.irs.simulation.space.grid.Corner
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.SquareGrid
import it.irs.simulation.space.grid.SquareGrid.Companion.DEFAULT_RADIUS
import it.irs.simulation.space.grid.Token
import kotlin.random.Random

class RandomGridGenerator(
  private val dimensions: Int,
  private val numObstacles: Int,
  private val random: Random,
) {
  fun randomGrid(): SquareGrid {
    val cells = mutableSetOf<Cell>()
    val tokens = mutableSetOf<Token>()

    val lightCorner = Corner.random()
    val lightPosition = lightCorner.point(dimensions)
    tokens.add(Light(lightPosition, lightColorToFollow))

    val startPosition = lightCorner.opposite().point(dimensions)
    tokens.add(Start(startPosition))

    val obstaclePositions = mutableSetOf<Point>()
    repeat(numObstacles) {
      var p: Point
      do {
        p = Point(random.nextInt(dimensions), random.nextInt(dimensions))
      } while (isValidObstaclePosition(p, obstaclePositions, lightPosition, startPosition))
      obstaclePositions.add(p)
      cells.add(Obstacle(p))
    }

    for (y in 0 until dimensions) {
      for (x in 0 until dimensions) {
        val point = Point(x, y)
        if (point !in obstaclePositions) {
          cells.add(Clear(point))
        }
      }
    }

    return SquareGrid(dimensions, cells, tokens)
  }

  companion object {
    fun isValidObstaclePosition(
      p: Point,
      obstaclePositions: Set<Point>,
      lightPosition: Point,
      startPosition: Point,
    ): Boolean =
      obstaclePositions.contains(p) ||
        p == lightPosition ||
        p == startPosition ||
        p in startPosition.neighbors(DEFAULT_RADIUS)
  }
}
