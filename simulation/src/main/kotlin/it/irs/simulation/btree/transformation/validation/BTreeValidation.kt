package it.irs.simulation.btree.transformation.validation

import arrow.core.Either
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.node.branch.CompositeNode

object BTreeValidation {
  private val logger: KLogger = KotlinLogging.logger {}

  fun <E> BehaviorTree<E>.isValid(
    rules: List<Rule>,
  ): Either<Rule, Boolean> where E : Environment<*> {
    tailrec fun validate(stack: List<TreeNode<E>>): Either<Rule, Boolean> {
      when {
        stack.isEmpty() -> {
          logger.trace { "The tree is valid" }
          return Either.Right(true)
        }

        else -> {
          val currentNode = stack.first()
          val violatedRule = rules.firstOrNull { !it.check(currentNode) }

          return if (violatedRule != null) {
            logger.trace { "Violation of $violatedRule rule found" }
            Either.Left(violatedRule)
          } else {
            val remainingNodes = stack.drop(1)
            val updatedStack =
              if (currentNode is CompositeNode<E>) {
                currentNode.children + remainingNodes
              } else {
                remainingNodes
              }

            validate(updatedStack)
          }
        }
      }
    }

    return validate(listOf(this.root))
  }
}
