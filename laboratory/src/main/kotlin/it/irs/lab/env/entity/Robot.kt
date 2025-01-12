package it.irs.lab.env.entity

import it.irs.lab.blackboard.RobotWrapper.RobotState.position
import it.irs.lab.env.GridWorld
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.PlanFollower
import it.irs.simulation.entity.Agent
import it.irs.simulation.space.grid.Direction
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.plus

data class Robot(
  override val bb: Blackboard,
  override val btree: BehaviorTree<GridWorld>,
) : Agent<GridWorld> {
  constructor(btree: BehaviorTree<GridWorld>) : this(Blackboard.create(), btree)

  override fun addBehaviorTree(btree: BehaviorTree<GridWorld>): PlanFollower<GridWorld> =
    copy(btree = btree)

  fun move(turnDirection: Direction?): Point? {
    val oldPosition = this.position()
    return turnDirection.let { it?.let { it1 -> oldPosition?.plus(it1.point) } }
  }

  fun matchesInDirections(
    directions: Set<Direction?>,
    potentialMatches: Set<Point>,
  ): Set<Point?>? {
    val toMatch = this.position() ?: return null
    val neighbours = directions.flatMap { listOf(it?.let { it1 -> toMatch.neighbor(it1) }) }
    return neighbours.toSet().intersect(potentialMatches)
  }
}
