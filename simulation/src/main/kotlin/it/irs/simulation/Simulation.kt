package it.irs.simulation

import it.irs.simulation.fsm.StateMachine

interface IRunIterations<E> where E : Environment<*> {
  fun tick(env: E): Simulation<E>

  fun isTerminated(): Boolean
}

interface SimulationStatistics

interface Simulation<E> : IRunIterations<E> where E : Environment<*> {
  val fsm: StateMachine<E>
  val virtualTime: Long

  fun execute(): Simulation<E> = runUntilStopped(this)

  fun stepStatistics(env: E)

  fun finalStatistics(): SimulationStatistics

  private tailrec fun runUntilStopped(sim: Simulation<E>): Simulation<E> {
    val updatedEnv = sim.fsm.currentState.env
    stepStatistics(updatedEnv)
    return if (sim.isTerminated()) {
      sim
    } else {
      runUntilStopped(sim.tick(updatedEnv))
    }
  }
}
