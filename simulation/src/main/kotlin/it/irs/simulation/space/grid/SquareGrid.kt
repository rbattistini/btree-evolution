package it.irs.simulation.space.grid

import it.irs.simulation.space.EnvironmentSpace

data class SquareGrid(
  val dimension: Int,
  val cells: Set<Cell>,
  override val tokens: Set<Token>,
) : EnvironmentSpace,
  TokenHolder {
  override fun toString(): String =
    cells.chunked(dimension).joinToString(separator = "\n") { row ->
      row.joinToString(separator = " | ", prefix = " ") { it.toString() }
    }

  override fun contains(p: Point): Boolean = cells.map { it.p }.contains(p)

  fun boundaries(
    p: Point?,
    radius: Int = DEFAULT_RADIUS,
  ): Set<Point> {
    val minX = cells.minOfOrNull { it.p.x } ?: 0
    val minY = cells.minOfOrNull { it.p.y } ?: 0
    val maxX = cells.maxOfOrNull { it.p.x } ?: (dimension - 1)
    val maxY = cells.maxOfOrNull { it.p.y } ?: (dimension - 1)

    if (p == null) return emptySet()

    return p
      .neighbors(radius)
      .plus(p)
      .filter { it.x < minX || it.x > maxX || it.y < minY || it.y > maxY }
      .toSet()
  }

  fun addTokens(tokens: Set<Token>): SquareGrid = copy(tokens = this.tokens.plus(tokens))

  companion object {
    const val DEFAULT_RADIUS = 1

    fun fromReferencePoint(
      reference: Point?,
      startingSet: Set<Point>,
      radius: Int,
    ): Set<Point> {
      if (reference == null) return emptySet()

      return reference
        .neighbors(radius)
        .filter { startingSet.contains(it) }
        .toSet()
    }
  }
}
