package it.irs.lab.fsm.robotState

import it.irs.lab.blackboard.RobotWrapper.RobotState.isIdle
import it.irs.lab.blackboard.RobotWrapper.RobotStatistics.addIdleStep
import it.irs.lab.env.GridWorld

data class Idle(
  override val env: GridWorld,
) : RobotState() {
  override fun onUpdate(env: GridWorld): RSState {
    val updatedEnv =
      env
        .let(::tickActiveRobot)
        .let(::writePreviousPosition)
        .let(::checkIfMoving)
        .let(::checkIfRevisiting)

    val activeRobot = updatedEnv.activeRobot()
    val finalEnv = updatedEnv.updateActiveRobot(activeRobot.addIdleStep()) ?: updatedEnv

    return if (activeRobot.isIdle() == true) {
      copy(env = finalEnv)
    } else {
      Moving(finalEnv)
    }
  }
}
