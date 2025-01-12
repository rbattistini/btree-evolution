package it.irs.lab.btree.transformation.randomization

import arrow.core.Either
import io.kotest.matchers.shouldBe
import it.irs.lab.ExperimentConfig.DEFAULT_DIMENSION
import it.irs.lab.btree.GridWorldBTree
import it.irs.lab.btree.GridWorldLeafNodeRegistry.moveForward
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnRandomly
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnToAvoidStored
import it.irs.lab.btree.GridWorldLeafNodeRegistry.turnToFollowStored
import it.irs.lab.btree.node.CheckFor
import it.irs.lab.btree.node.TurnTo
import it.irs.lab.env.GridWorld
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.randomization.UnaryRandomTool
import it.irs.simulation.space.grid.GridEntity
import it.irs.simulation.space.grid.Orientation

object BTRandomizationSpecHelper {
  val registry =
    LeafNodeRegistry(
      buildMap {
        put("moveForward", moveForward)
        put("turnRandomly", turnRandomly)
        put("turnToAvoidStored", turnToAvoidStored)
        put("turnToFollowStored", turnToFollowStored)

        setOf(Orientation.Left, Orientation.Right, Orientation.Forward).forEach { orientation ->
          if (orientation != Orientation.Forward) {
            val node = TurnTo(orientation)
            put(node.name, node)
          } else {
            put(
              "checkForGreenLightAhead${orientation.name}",
              CheckFor(GridEntity.GreenLight, orientation, DEFAULT_DIMENSION),
            )
          }

          GridEntity.entries.forEach { entity ->
            val node = CheckFor(entity, orientation, DEFAULT_DIMENSION)
            put(node.name, node)
          }
        }
      },
    )

  fun transform(
    btree: GridWorldBTree,
    expectedRes: Either<String, GridWorldBTree>,
    tool: UnaryRandomTool<GridWorld>,
  ) {
    println("Expected: \n${expectedRes.getOrNull()?.string}")
    val res = tool.transform(btree)
    println("Got: \n${res.string}")
    res.toString() shouldBe expectedRes.getOrNull().toString()
  }
}
