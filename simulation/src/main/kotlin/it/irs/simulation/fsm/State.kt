package it.irs.simulation.fsm

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import it.irs.simulation.Environment

interface State<E> where E : Environment<*> {
  val name: String
    get() = this.javaClass.simpleName

  val logger: KLogger
    get() = KotlinLogging.logger {}

  val env: E

  fun onUpdate(env: E): State<E> {
    logger.debug { "Updating $name State" }
    return this
  }
}
