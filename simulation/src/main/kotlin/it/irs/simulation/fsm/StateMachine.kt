package it.irs.simulation.fsm

import it.irs.simulation.Environment

data class StateMachine<E>(
  val currentState: State<E>,
) where E : Environment<*> {
  fun tick(env: E): StateMachine<E> = copy(currentState = currentState.onUpdate(env))
}
