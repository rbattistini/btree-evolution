package it.irs.lab.fsm

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.types.shouldBeTypeOf
import it.irs.lab.env.GridWorld
import it.irs.lab.fsm.StateMachineSpecHelper.Green
import it.irs.lab.fsm.StateMachineSpecHelper.Red
import it.irs.lab.fsm.StateMachineSpecHelper.Yellow
import it.irs.simulation.blackboard.Blackboard
import it.irs.simulation.fsm.StateMachine

class StateMachineSpec :
  ShouldSpec({

    tailrec fun runUntilMaxSteps(
      fsm: StateMachine<GridWorld>,
      currentStep: Int = 0,
      maxSteps: Int,
    ): StateMachine<GridWorld> =
      if (currentStep >= maxSteps) {
        fsm
      } else {
        val updatedFsm = fsm.tick(fsm.currentState.env)
        runUntilMaxSteps(updatedFsm, currentStep + 1, maxSteps)
      }

    context("Traffic Light FSM") {
      val bb = Blackboard.create()
      val fakeEnv = GridWorld(bb.write("tickLimit" to 3))
      val redState = Red(fakeEnv)
      val stateMachine = StateMachine(redState)

      should("start in Red state") { stateMachine.currentState.shouldBeTypeOf<Red>() }

      should("transition from Red to Green after five ticks") {
        val updatedFsm = runUntilMaxSteps(stateMachine, maxSteps = 5)
        updatedFsm.currentState.shouldBeTypeOf<Green>()
      }

      should("transition from Green to Yellow after ten ticks") {
        val updatedFsm = runUntilMaxSteps(stateMachine, maxSteps = 10)
        updatedFsm.currentState.shouldBeTypeOf<Yellow>()
      }

      should("complete a full cycle (Red -> Green -> Yellow -> Red) in fifteen ticks") {
        val updatedFsm = runUntilMaxSteps(stateMachine, maxSteps = 15)
        updatedFsm.currentState.shouldBeTypeOf<Red>()
      }
    }
  })
