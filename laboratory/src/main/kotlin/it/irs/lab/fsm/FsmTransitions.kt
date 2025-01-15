package it.irs.lab.fsm

import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.env.GridExtensions.lightCells
import it.irs.lab.env.GridWorld
import it.irs.lab.env.GridWorld.Companion.lightColorToFollow

// object FsmTransitions {
//  fun goalReached(env: GridWorld): Boolean {
//    val robotPosition: Point? = env.activeRobot().position()
//    val lightPositions: Set<Point> =
//      env.space.tokens
//        .filterIsInstance<Light>()
//        .map { it.p }
//        .toSet()
//    return robotPosition in lightPositions
//  }
// }

object FsmTransitions {
  fun goalReached(env: GridWorld): Boolean {
    val robotPosition = env.activeRobot().position()
    val lightPositions = env.space.lightCells(lightColorToFollow)
    return robotPosition in lightPositions
  }
}
