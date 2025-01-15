package it.irs.lab.fsm.robotState

import it.irs.lab.blackboard.RobotWrapper.RobotState.isIdle
import it.irs.lab.env.GridWorld
import it.irs.lab.fsm.FsmTransitions.goalReached

data class Moving(
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

    return when {
      goalReached(updatedEnv) -> GoalReached(updatedEnv)
      activeRobot.isIdle() == true -> Idle(updatedEnv)
      else -> copy(env = updatedEnv)
    }
  }
}
