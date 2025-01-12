package it.irs.simulation.btree.node.result

import it.irs.simulation.Environment
import it.irs.simulation.btree.node.BState

interface TickResult<E> where E : Environment<*> {
  val env: E
  val state: BState

  companion object {
    fun <E> fromResult(
      env: E,
      state: BState,
    ) where E : Environment<*> =
      object : TickResult<E> {
        override val env: E = env
        override val state: BState = state
      }
  }
}
