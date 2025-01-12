package it.irs.simulation.space.grid

import kotlin.math.abs

data class Point(
  val x: Int,
  val y: Int,
) {
  fun manhattanDistance(p2: Point): Int = abs(p2.x - this.x) + abs(p2.y - this.y)

  fun neighbor(direction: Direction): Point = this + direction.point

  fun neighbors(radius: Int): Set<Point> {
    require(radius >= 1) { "Radius must be at least 1" }
    return (1..radius)
      .flatMap { r ->
        listOf(
          Point(this.x + r, this.y),
          Point(this.x - r, this.y),
          Point(this.x, this.y + r),
          Point(this.x, this.y - r),
        )
      }.toSet()
  }
}

operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)

operator fun Point.minus(other: Point) = Point(x - other.x, y - other.y)
