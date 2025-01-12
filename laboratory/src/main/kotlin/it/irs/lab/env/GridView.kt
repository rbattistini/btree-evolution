package it.irs.lab.env

import it.irs.lab.env.GridExtensions.lightCells
import it.irs.lab.env.GridExtensions.obstacles
import it.irs.lab.env.cell.Clear
import it.irs.lab.env.cell.Obstacle
import it.irs.lab.env.token.Light
import it.irs.lab.env.token.Start
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.SquareGrid
import java.awt.Color
import java.awt.image.BufferedImage

@Suppress("MagicNumber")
object GridView {
  fun toColor(): Color = robotAgent

  fun SquareGrid.toAscii(
    markedPoints: Set<Point>? = null,
    markerChar: Char = DEFAULT_ROBOT_CHAR,
  ): String {
    val gridArray = Array(cells.maxOf { it.p.y } + 1) { CharArray(dimension) { ' ' } }

    obstacles().forEach { point ->
      gridArray[point.y][point.x] = 'O'
    }

    cells.filter { it.p !in obstacles() }.forEach { cell ->
      gridArray[cell.p.y][cell.p.x] = '.'
    }

    tokens
      .filterIsInstance<Start>()
      .map { it.p }
      .forEach { point ->
        gridArray[point.y][point.x] = 'S'
      }

    lightCells(Color.RED).forEach { point ->
      gridArray[point.y][point.x] = 'R'
    }

    lightCells(Color.GREEN).forEach { point ->
      gridArray[point.y][point.x] = 'G'
    }

    markedPoints?.forEach { point ->
      gridArray[point.y][point.x] = markerChar
    }

    return gridArray.joinToString(" |\n") { row -> "| " + row.joinToString(" | ") } + " |"
  }

  fun SquareGrid.toImage(): BufferedImage {
    val image = BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_RGB)

    cells.forEach { cell ->
      val color =
        when (cell) {
          is Obstacle -> obstacleCell
          is Clear -> clearCell
          else -> Color.GRAY
        }
      image.setRGB(cell.p.x, cell.p.y, color.rgb)
    }

    tokens.forEach { token ->
      val color =
        when (token) {
          is Light -> token.c
          is Start -> startCell
          else -> Color.BLACK
        }
      image.setRGB(token.p.x, token.p.y, color.rgb)
    }

    return image
  }

  private const val DEFAULT_ROBOT_CHAR = 'B'

  private val obstacleCell = Color(179, 172, 172)
  private val clearCell = Color(241, 223, 190)
  private val startCell = Color.ORANGE
  private val robotAgent = Color(56, 78, 192)
}
