package it.irs.lab.env

import it.irs.lab.blackboard.GridWorldWrapper.activeRobotId
import it.irs.lab.env.cell.Clear
import it.irs.lab.env.cell.Obstacle
import it.irs.lab.env.entity.Robot
import it.irs.lab.env.token.Light
import it.irs.lab.env.token.Start
import it.irs.simulation.Environment
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.entity.Entity
import it.irs.simulation.space.grid.GridBuilder.Companion.grid
import it.irs.simulation.space.grid.Point
import it.irs.simulation.space.grid.SquareGrid
import java.awt.Color

class GridWorld(
  override val bb: Blackboard,
  override val space: SquareGrid,
  override val entities: Map<String, Entity>,
) : Environment<SquareGrid> {
  constructor() : this(Blackboard.create(), defaultGrid, emptyMap())
  constructor(bb: Blackboard) : this(bb, defaultGrid, emptyMap())

  companion object {
    val lightColorToFollow: Color = Color.GREEN
    val lightColorToAvoid: Color = Color.RED

    val defaultGrid =
      grid(
        6,
        mapOf(
          'o' to { p: Point -> Obstacle(p) },
          'c' to { p: Point -> Clear(p) },
        ),
      ) {
        'c' + 'c' + 'o' + 'c' + 'c' + 'c' -
          'c' + 'o' + 'c' + 'c' + 'c' + 'c' -
          'c' + 'o' + 'c' + 'c' + 'c' + 'c' -
          'c' + 'o' + 'c' + 'o' + 'c' + 'c' -
          'c' + 'c' + 'c' + 'o' + 'c' + 'c' -
          'c' + 'o' + 'c' + 'c' + 'c' + 'c'
      }.addTokens(
        setOf(
          Start(Point(0, 0)),
          Light(
            p = Point(5, 5),
            c = Color.GREEN,
          ),
        ),
      )
  }

  fun activeRobot(): Robot {
    val activeRobotId = this.bb.activeRobotId()
    return this.entities[activeRobotId] as Robot
  }

  fun updateActiveRobot(updatedRobot: Robot?): GridWorld? {
    if (updatedRobot == null) return null

    val activeRobotId = this.bb.activeRobotId()
    return if (activeRobotId != null) {
      copy(ent = this.entities.plus(activeRobotId to updatedRobot))
    } else {
      null
    }
  }

  override fun copy(
    bb: Blackboard,
    space: SquareGrid,
    ent: Map<String, Entity>,
  ): GridWorld = GridWorld(bb, space, ent)

  override fun addEntity(
    entityId: String,
    entity: Entity,
  ): GridWorld = copy(ent = entities.plus(entityId to entity))
}
