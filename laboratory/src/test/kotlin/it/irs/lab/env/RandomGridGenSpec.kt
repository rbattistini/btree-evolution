package it.irs.lab.env

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.ExperimentConfig.DEFAULT_SEED
import it.irs.lab.env.GridView.toAscii
import it.irs.lab.env.cell.Clear
import it.irs.lab.env.cell.Obstacle
import it.irs.lab.env.random.GridValidator.genValidGrid
import it.irs.lab.env.random.RandomGridGenerator
import it.irs.lab.env.token.Light
import it.irs.lab.env.token.Start
import it.irs.simulation.space.grid.GridBuilder.Companion.grid
import it.irs.simulation.space.grid.Point
import java.awt.Color
import kotlin.random.Random
import kotlin.test.DefaultAsserter.fail

class RandomGridGenSpec :
  ShouldSpec({
    val testNumObstacles = 6
    val dimensions = 4

    val gridGenerator: () -> RandomGridGenerator = {
      RandomGridGenerator(
        dimensions,
        testNumObstacles,
        Random(DEFAULT_SEED),
      )
    }

    should("generate a random grid according to the given parameters") {
      val randomGrid = gridGenerator().randomGrid()

      val expectedGrid =
        grid(
          dimensions,
          mapOf(
            'o' to { p: Point -> Obstacle(p) },
            'c' to { p: Point -> Clear(p) },
          ),
        ) {
          'o' + 'o' + 'c' + 'c' -
            'o' + 'c' + 'c' + 'c' -
            'c' + 'o' + 'o' + 'c' -
            'c' + 'c' + 'c' + 'o'
        }

      val expectedLights =
        setOf(
          Light(
            p = Point(0, 3),
            c = Color.GREEN,
          ),
          Start(
            p = Point(3, 0),
          ),
        )

      val expectedGridWithLights = expectedGrid.addTokens(expectedLights)

      println(randomGrid.toAscii())
      println()
      println(expectedGridWithLights.toAscii())

      randomGrid shouldBe expectedGridWithLights
    }

    should("regenerate the grid if no path to a light could be found") {
      val validatedGrid = genValidGrid(gridGenerator(), maxAttempts = 5)
      if (validatedGrid.isLeft()) {
        fail("Could not generate a valid grid")
      }

      val expectedTokens =
        setOf(
          Light(
            p = Point(0, 3),
            c = Color.GREEN,
          ),
          Start(
            p = Point(3, 0),
          ),
        )

      val expectedGrid =
        grid(
          dimensions,
          mapOf(
            'o' to { p: Point -> Obstacle(p) },
            'c' to { p: Point -> Clear(p) },
          ),
        ) {
          'o' + 'o' + 'c' + 'c' -
            'c' + 'c' + 'o' + 'c' -
            'o' + 'c' + 'c' + 'c' -
            'c' + 'c' + 'o' + 'o'
        }

      val expectedGridWithTokens = expectedGrid.addTokens(expectedTokens)

      println(validatedGrid.getOrNull()?.toAscii())
      println()
      println(expectedGridWithTokens.toAscii())

      validatedGrid.getOrNull() shouldBe expectedGridWithTokens
    }
  })
