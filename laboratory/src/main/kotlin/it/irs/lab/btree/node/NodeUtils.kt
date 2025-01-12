package it.irs.lab.btree.node

import it.irs.lab.btree.GridWorldTickResult
import it.irs.lab.env.GridWorld
import it.irs.lab.env.entity.Robot
import it.irs.simulation.btree.node.BState
import it.irs.simulation.btree.node.result.TickResult

object NodeUtils {
  fun resultOfRobotAction(
    env: GridWorld,
    updatedRobot: Robot?,
  ): GridWorldTickResult {
    val updatedEnv = env.updateActiveRobot(updatedRobot)
    return TickResult.fromResult(
      updatedEnv ?: env,
      if (updatedEnv != null) BState.Success else BState.Failure,
    )
  }
}
