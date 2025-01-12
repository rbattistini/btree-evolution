package it.irs.simulation.space.grid

/**
 * Builds a grid of cells based on the given configuration.
 *
 * @param cols The number of columns in the grid.
 * @param cellType A map that associates characters with their corresponding cell factories.
 * @return A new instance of the Grid class representing the built grid.
 */
class GridBuilder(
  private val cols: Int,
  private val cellType: Map<
    Char,
    (
      Point,
    ) -> Cell,
    >,
) {
  private val cells = mutableListOf<Cell>()
  private var currentRow = 0

  operator fun Char.plus(other: Char): GridBuilder {
    addCell(this.toCellType(getPoint()))
    addCell(other.toCellType(getPoint()))
    return this@GridBuilder
  }

  operator fun GridBuilder.minus(other: Char): GridBuilder {
    currentRow++
    addCell(other.toCellType(getPoint()))
    return this
  }

  operator fun GridBuilder.plus(other: Char): GridBuilder {
    addCell(other.toCellType(getPoint()))
    return this
  }

  fun build(): SquareGrid = SquareGrid(cols, cells.toSet(), emptySet())

  private fun getPoint(): Point = Point(cells.size % cols, currentRow)

  private fun addCell(cell: Cell?) = cell?.let { cells.add(it) }

  private fun Char.toCellType(p: Point): Cell? = cellType[this]?.invoke(p)

  companion object {
    fun grid(
      cols: Int,
      cellType: Map<
        Char,
        (
          Point,
        ) -> Cell,
        >,
      init: GridBuilder.() -> Unit,
    ): SquareGrid {
      val builder =
        GridBuilder(cols, cellType)
      builder.apply(init)
      return builder.build()
    }
  }
}
