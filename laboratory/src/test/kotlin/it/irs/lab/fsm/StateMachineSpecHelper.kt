package it.irs.lab.fsm

import it.irs.lab.env.GridWorld
import it.irs.lab.fsm.robotState.RSState
import it.irs.simulation.Environment

object StateMachineSpecHelper {
  abstract class SemaphoreState : RSState {
    fun updateSemaphore(env: GridWorld): GridWorld {
      val tickLimit: Int? = env.bb.read("tickLimit") as Int?
      val tickCount: Int? = env.bb.read("tickCount") as Int?
      return if (tickCount != null) {
        return if (tickLimit != null && tickCount >= tickLimit) {
          env.copy(env.bb.write("tickCount" to 0))
        } else {
          env.copy(env.bb.write("tickCount" to tickCount + 1))
        }
      } else {
        env.copy(env.bb.write("tickCount" to 0))
      }
    }
  }

  data class Red(
    override val env: GridWorld,
  ) : SemaphoreState() {
    override fun onUpdate(env: GridWorld): RSState {
      val updatedEnv = updateSemaphore(env)
      return if (tickBasedTransition(updatedEnv)) {
        Green(updatedEnv)
      } else {
        copy(env = updatedEnv)
      }
    }
  }

  data class Green(
    override val env: GridWorld,
  ) : SemaphoreState() {
    override fun onUpdate(env: GridWorld): RSState {
      val updatedEnv = updateSemaphore(env)
      return if (tickBasedTransition(updatedEnv)) {
        Yellow(updatedEnv)
      } else {
        copy(env = updatedEnv)
      }
    }
  }

  data class Yellow(
    override val env: GridWorld,
  ) : SemaphoreState() {
    override fun onUpdate(env: GridWorld): RSState {
      val updatedEnv = updateSemaphore(env)
      return if (tickBasedTransition(updatedEnv)) {
        Red(updatedEnv)
      } else {
        copy(env = updatedEnv)
      }
    }
  }

  fun <E : Environment<*>> tickBasedTransition(env: E): Boolean {
    val tickLimit: Int? = env.bb.read("tickLimit") as Int?
    val tickCount: Int? = env.bb.read("tickCount") as Int?
    return if (tickCount != null && tickLimit != null) {
      tickCount >= tickLimit
    } else {
      false
    }
  }
}
