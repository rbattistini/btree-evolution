package it.irs.lab.env.random

import arrow.core.Either
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import it.irs.lab.env.GridExtensions.lightCells
import it.irs.lab.env.GridExtensions.obstacles
import it.irs.lab.env.GridExtensions.startPositions
import it.irs.lab.env.GridView.toAscii
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.SquareGrid
import java.awt.Color

object GridValidator {
  private val logger: KLogger = KotlinLogging.logger {}

  private const val START_VALIDATION_ATTEMPT = 0
  private const val MAX_VALIDATION_ATTEMPTS = 3
  private const val DEFAULT_NEIGHBOURS_RADIUS = 1

  private tailrec fun containsPathToLight(
    currentList: List<Point>,
    lightPositions: Set<Point>,
    visited: Set<Point>,
    squareGrid: SquareGrid,
  ): Boolean =
    when {
      currentList.isEmpty() -> false
      currentList.first() in lightPositions -> true
      else -> {
        val current = currentList.first()
        val updatedVisited = visited + current
        val neighbors =
          current
            .neighbors(DEFAULT_NEIGHBOURS_RADIUS)
            .filter { neighbor ->
              squareGrid.contains(neighbor) &&
                neighbor !in squareGrid.lightCells(Color.RED) &&
                neighbor !in squareGrid.obstacles() &&
                neighbor !in visited
            }
        containsPathToLight(
          currentList.drop(1) + neighbors,
          lightPositions,
          updatedVisited,
          squareGrid,
        )
      }
    }

  tailrec fun genValidGrid(
    randomGridGenerator: RandomGridGenerator,
    attempt: Int = START_VALIDATION_ATTEMPT,
    maxAttempts: Int = MAX_VALIDATION_ATTEMPTS,
  ): Either<String, SquareGrid> {
    if (attempt > maxAttempts) return Either.Left("No valid grid found after $attempt attempts")

    val randomGrid = randomGridGenerator.randomGrid()
    val targetPoints = randomGrid.lightCells(Color.GREEN)
    val pathExists =
      containsPathToLight(
        randomGrid.startPositions().toList(),
        targetPoints,
        emptySet(),
        randomGrid,
      )

    return if (pathExists) {
      Either.Right(randomGrid)
    } else {
      logger.info { "\n${randomGrid.toAscii()}" }
      logger.info { "Attempt $attempt failed. Retrying..." }
      genValidGrid(randomGridGenerator, attempt + 1, maxAttempts)
    }
  }
}
