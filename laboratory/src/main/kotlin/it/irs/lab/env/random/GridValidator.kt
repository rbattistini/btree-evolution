package it.irs.lab.env.random

import arrow.core.Either
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import it.irs.lab.env.GridExtensions.lightCells
import it.irs.lab.env.GridExtensions.obstacles
import it.irs.lab.env.GridExtensions.startPositions
import it.irs.lab.env.GridView.toAscii
import it.irs.lab.env.GridWorld.Companion.lightColorToAvoid
import it.irs.lab.env.GridWorld.Companion.lightColorToFollow
import it.irs.lab.experiment.config.DefaultConfig.DEFAULT_NEIGHBOURS_RADIUS
import it.irs.lab.experiment.config.DefaultConfig.MAX_VALIDATION_ATTEMPTS
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.SquareGrid
import kotlin.random.Random

class GridValidator(
  val maxValidationAttempts: Int = MAX_VALIDATION_ATTEMPTS,
  val neighbourRadius: Int = DEFAULT_NEIGHBOURS_RADIUS,
) {
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
            .neighbors(neighbourRadius)
            .filter { neighbor ->
              squareGrid.contains(neighbor) &&
                neighbor !in squareGrid.lightCells(lightColorToAvoid) &&
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
    random: Random,
    attempt: Int = START_VALIDATION_ATTEMPT,
    maxAttempts: Int = maxValidationAttempts,
  ): Either<String, SquareGrid> {
    if (attempt > maxAttempts) {
      logger.warn { "Defaulting to standard grid" }
      return Either.Left("No valid grid found after $attempt attempts")
    }

    val randomGrid = randomGridGenerator.randomGrid(random)
    val targetPoints = randomGrid.lightCells(lightColorToFollow)
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
      logger.trace { "\n${randomGrid.toAscii()}" }
      logger.trace { "Attempt $attempt failed. Retrying..." }
      genValidGrid(randomGridGenerator, random, attempt + 1, maxAttempts)
    }
  }

  companion object {
    private val logger: KLogger = logger {}
    const val START_VALIDATION_ATTEMPT = 0
  }
}
