package it.irs.lab.btree.transformation

import arrow.core.Either
import io.kotest.matchers.shouldBe
import it.irs.lab.entity.RobotSpecHelper.DEFAULT_TEST_SEED
import it.irs.lab.env.GridWorld
import it.irs.lab.genetic.Exp1GenePool.ofExp1
import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.node.leaf.ActionNode
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.reparation.BTreeReparation
import it.irs.simulation.btree.transformation.reparation.BTreeReparation.Companion.repair
import it.irs.simulation.btree.transformation.reparation.ConditionNotLastChild
import it.irs.simulation.btree.transformation.reparation.ControlNodeMustHaveChildren
import it.irs.simulation.btree.transformation.reparation.NoConsecutiveControlNodes
import it.irs.simulation.btree.transformation.reparation.NoIdenticalAdjacentConditions
import it.irs.simulation.btree.transformation.reparation.RepairTool
import it.irs.simulation.btree.transformation.validation.BTreeRules
import it.irs.simulation.btree.transformation.validation.BTreeValidation.isValid
import it.irs.simulation.btree.transformation.validation.Rule
import kotlin.random.Random

object TransformationSpecHelper {
  val rules: List<Rule> =
    listOf(
      BTreeRules.controlNodeMustHaveChildren,
      BTreeRules.noConsecutiveControlNodes,
      BTreeRules.conditionsNotLastChild,
      BTreeRules.noIdenticalAdjacentConditions,
    )

  private val nodeFactory = {
    LeafNodeRegistry
      .ofExp1(Random(DEFAULT_TEST_SEED))
      .nodes
      .filter { it.value is ActionNode<*> }
      .values
      .random(Random(DEFAULT_TEST_SEED))
  }

  private const val KEEP_CHILDREN = true

  private val toolset: Map<Rule, RepairTool> =
    mapOf(
      BTreeRules.noConsecutiveControlNodes to NoConsecutiveControlNodes(KEEP_CHILDREN),
      BTreeRules.noIdenticalAdjacentConditions to NoIdenticalAdjacentConditions(KEEP_CHILDREN),
      BTreeRules.controlNodeMustHaveChildren to ControlNodeMustHaveChildren(),
      BTreeRules.conditionsNotLastChild to ConditionNotLastChild(),
    )

  const val TEST_REPAIR_ATTEMPTS = 3

  val conRepair = BTreeReparation(rules, toolset, nodeFactory)

  fun repair(
    btree: BehaviorTree<GridWorld>,
    expectedRes: Either<String, BehaviorTree<GridWorld>>,
  ) {
    println(btree.string)
    println(expectedRes.getOrNull()?.string)
    val validationRes = btree.isValid(rules)
    if (validationRes is Either.Left) {
      val repRes = btree.repair(validationRes.value, toolset, nodeFactory)
      if (repRes is Either.Left) {
        println("Reparation failed due to: ${repRes.value}")
      } else {
        println(repRes.getOrNull()?.string)
        repRes.toString() shouldBe expectedRes.toString()
      }
    }
  }

  fun <E> validate(
    btree: BehaviorTree<E>,
    expectedRes: Either<Rule, Boolean>,
  ) where E : Environment<*> {
    val validationResult = btree.isValid(rules)
    if (validationResult is Either.Left) {
      println("Validation failed due to rule: ${validationResult.value}")
    }
    validationResult shouldBe expectedRes
  }
}
