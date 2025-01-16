package it.irs.lab.sim

import it.irs.simulation.SimulationStatistics

data class GridWorldSimStatistics(
  val initialDistanceToLight: Int,
  val finalDistanceToLight: Int,
  val collisionSteps: Int,
  val idleSteps: Int,
  val backtrackingSteps: Int,
  val totalSteps: Int,
  val treeSize: Int,
) : SimulationStatistics
