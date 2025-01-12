package it.irs.simulation.space.grid

import it.irs.simulation.SimulationUtils.DEFAULT_SEED
import kotlin.random.Random

enum class Corner(
  val point: (dimension: Int) -> Point,
) {
  TOP_LEFT({ Point(0, 0) }),
  TOP_RIGHT({ dimension -> Point(dimension - 1, 0) }),
  BOTTOM_LEFT({ dimension -> Point(0, dimension - 1) }),
  BOTTOM_RIGHT({ dimension -> Point(dimension - 1, dimension - 1) }),
  ;

  fun opposite(): Corner =
    when (this) {
      TOP_LEFT -> BOTTOM_RIGHT
      TOP_RIGHT -> BOTTOM_LEFT
      BOTTOM_LEFT -> TOP_RIGHT
      BOTTOM_RIGHT -> TOP_LEFT
    }

  companion object {
    fun allCorners(dimension: Int): Map<Corner, Point> =
      entries.associateWith { it.point(dimension) }

    fun random(random: Random = Random(DEFAULT_SEED)): Corner {
      val corner = entries.toTypedArray().random(random)
      return corner
    }
  }
}
