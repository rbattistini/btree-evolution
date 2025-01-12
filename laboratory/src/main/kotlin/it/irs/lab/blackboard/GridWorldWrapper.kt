package it.irs.lab.blackboard

import it.irs.simulation.blackboard.Blackboard

object GridWorldWrapper {
  fun Blackboard.activeRobotId() = this.read("activeRobotId") as String?

  fun Blackboard.writeActiveRobotId(id: String) = this.write("activeRobotId" to id)
}
