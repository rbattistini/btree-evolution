package it.irs.lab.env

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import it.irs.lab.entity.RobotSpecHelper.DEFAULT_TEST_SEED
import it.irs.lab.env.GridView.toAscii
import it.irs.lab.env.cell.Clear
import it.irs.lab.env.cell.Obstacle
import it.irs.lab.env.random.GridValidator
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
      )
    }

    should("generate a random grid according to the given parameters") {
      val randomGrid = gridGenerator().randomGrid(Random(DEFAULT_TEST_SEED))

      val expectedGrid =
        grid(
          dimensions,
          mapOf(
            'o' to { p: Point -> Obstacle(p) },
            'c' to { p: Point -> Clear(p) },
          ),
        ) {
          'c' + 'c' + 'c' + 'o' -
            'c' + 'c' + 'o' + 'o' -
            'o' + 'c' + 'c' + 'c' -
            'o' + 'o' + 'c' + 'c'
        }

      val expectedLights =
        setOf(
          Light(
            p = Point(0, 0),
            c = Color.GREEN,
          ),
          Start(
            p = Point(3, 3),
          ),
        )

      val expectedGridWithLights = expectedGrid.addTokens(expectedLights)

      println(randomGrid.toAscii())
      println()
      println(expectedGridWithLights.toAscii())

      randomGrid shouldBe expectedGridWithLights
    }

    should("regenerate the grid if no path to a light could be found") {
      val validGridGen = GridValidator()
      val validatedGrid =
        validGridGen.genValidGrid(
          gridGenerator(),
          Random(DEFAULT_TEST_SEED),
          maxAttempts = 5,
        )
      if (validatedGrid.isLeft()) {
        fail("Could not generate a valid grid")
      }

      val expectedTokens =
        setOf(
          Light(
            p = Point(0, 0),
            c = Color.GREEN,
          ),
          Start(
            p = Point(3, 3),
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
          'c' + 'c' + 'c' + 'o' -
            'c' + 'c' + 'o' + 'o' -
            'o' + 'c' + 'c' + 'c' -
            'o' + 'o' + 'c' + 'c'
        }

      val expectedGridWithTokens = expectedGrid.addTokens(expectedTokens)

      println(validatedGrid.getOrNull()?.toAscii())
      println()
      println(expectedGridWithTokens.toAscii())

      validatedGrid.getOrNull() shouldBe expectedGridWithTokens
    }
  })
