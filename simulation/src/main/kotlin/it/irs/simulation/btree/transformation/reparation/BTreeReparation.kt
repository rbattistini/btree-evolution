package it.irs.simulation.btree.transformation.reparation

import arrow.core.Either
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.transformation.validation.BTreeValidation.isValid
import it.irs.simulation.btree.transformation.validation.Rule

class BTreeReparation<E>(
  private val rules: List<Rule>,
  private val toolset: Map<Rule, RepairTool>,
  private val nodeFactory: () -> TreeNode<E>,
) where E : Environment<*> {
  tailrec fun repairWithRetry(
    btree: BehaviorTree<E>,
    attemptsLeft: Int,
  ): BehaviorTree<E> {
    if (attemptsLeft <= 0) {
      logger.warn { "Could not fully repair the tree" }
      return btree
    }

    return when (val r = btree.isValid(rules)) {
      is Either.Left ->
        when (val b = btree.repair(r.value, toolset, nodeFactory)) {
          is Either.Left -> btree
          is Either.Right -> repairWithRetry(b.value, attemptsLeft - 1)
        }

      else -> btree
    }
  }

  companion object {
    private val logger: KLogger = KotlinLogging.logger {}

    fun <E> BehaviorTree<E>.repair(
      rule: Rule,
      toolset: Map<Rule, RepairTool>,
      nodeFactory: () -> TreeNode<E>,
    ): Either<String, BehaviorTree<E>> where E : Environment<*> {
      val tool = toolset[rule]
      return if (tool != null) {
        logger.debug { "Trying to repair $this due to $rule violation" }
        when (tool) {
          is GenerativeRepairTool -> Either.Right(tool.repair(this, rule, nodeFactory))
          is DestructiveRepairTool -> Either.Right(tool.repair(this, rule))
        }
      } else {
        Either.Left("No tool found for rule $rule")
      }
    }
  }
}
