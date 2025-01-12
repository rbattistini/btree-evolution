package it.irs.simulation.space.grid

enum class Direction(
  val point: Point,
) {
  West(Point(-1, 0)),
  East(Point(1, 0)),
  North(Point(0, 1)),
  South(Point(0, -1)),
  ;

  fun opposite(): Direction =
    when (this) {
      West -> East
      East -> West
      North -> South
      South -> North
    }

  companion object {
    fun Point.toDirection(reference: Point?): Direction? {
      if (reference == null) return null
      return entries.find { it.point == this - reference }
    }

    fun getDirectionsFromPoints(
      reference: Point?,
      matches: Set<Point>?,
    ): Set<Direction>? {
      if (reference == null) return emptySet()
      val directionMap = entries.associateBy { reference + it.point }
      return matches?.mapNotNull { directionMap[it] }?.toSet()
    }
  }
}
