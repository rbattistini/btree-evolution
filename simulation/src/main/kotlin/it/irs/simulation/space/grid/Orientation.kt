package it.irs.simulation.space.grid

enum class Orientation {
  Left,
  Right,
  Forward,
  Backward,
  ;

  fun toDirection(reference: Point?): Direction? {
    if (reference == null) return null
    val offset =
      when (this) {
        Left -> Point(-1, 0)
        Right -> Point(1, 0)
        Forward -> Point(0, 1)
        Backward -> Point(0, -1)
      }
    return Direction.entries.find { it.point == offset }
  }
}
