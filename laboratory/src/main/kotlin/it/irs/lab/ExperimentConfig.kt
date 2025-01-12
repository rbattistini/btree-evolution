package it.irs.lab

import it.irs.lab.btree.GridWorldLeafNodeRegistry.of
import it.irs.simulation.btree.node.leaf.ActionNode
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.randomization.BTreeRandomAdditionTool
import it.irs.simulation.btree.transformation.randomization.BTreeRandomDeletionTool
import it.irs.simulation.btree.transformation.randomization.BTreeRandomModificationTool
import it.irs.simulation.btree.transformation.reparation.ConditionNotLastChild
import it.irs.simulation.btree.transformation.reparation.ControlNodeMustHaveChildren
import it.irs.simulation.btree.transformation.reparation.NoConsecutiveControlNodes
import it.irs.simulation.btree.transformation.reparation.NoIdenticalAdjacentConditions
import it.irs.simulation.btree.transformation.reparation.RepairTool
import it.irs.simulation.btree.transformation.validation.BTreeRules
import it.irs.simulation.btree.transformation.validation.Rule
import kotlin.random.Random

object ExperimentConfig {
  // Engine Parameters
  const val POP_SIZE = 30

  const val MAX_PHENOTYPE_AGE: Long = 500
  const val MUTATION_PROB = 0.5
  const val CROSSOVER_PROBABILITY = 0.6

  // Grid generation parameters
  const val DEFAULT_SEED = 42
  const val DEFAULT_DIMENSION = 7
  const val DEFAULT_NUM_OBSTACLES = 12

  // Fitness Function Parameters
  const val GOAL_REACHED_REWARD = 12.0
  const val COLLISION_PENALTY = 1.0
  const val REVISITED_CELL_PENALTY = 1.0
  const val IDLE_PENALTY = 1.0
  const val TREE_SIZE_PENALTY = 100.0
  const val MAX_TREE_SIZE = 10
  const val MAX_SIM_STEPS = 50
  const val SIM_RUNS = 3

  // Evolution Stream Parameters
  const val STEADY_FITNESS = 600
  const val MAX_EPISODES: Long = 800

  const val GENE_MAX_DEPTH = 2
  const val GENE_MIN_CHILDREN = 2
  const val GENE_MAX_CHILDREN = 3

  const val REPARATION_ATTEMPTS = 9

  const val ELITE_COUNT = 3
  const val TOURNAMENT_SAMPLE_SIZE = 5

  const val DELTA_TIME = 1
  const val DEFAULT_VIRTUAL_TIME: Long = 0

  val rules: List<Rule> =
    listOf(
      BTreeRules.controlNodeMustHaveChildren,
      BTreeRules.noConsecutiveControlNodes,
      BTreeRules.conditionsNotLastChild,
      BTreeRules.noIdenticalAdjacentConditions,
    )

  val toolset: Map<Rule, RepairTool> =
    mapOf(
      BTreeRules.noConsecutiveControlNodes to NoConsecutiveControlNodes(),
      BTreeRules.noIdenticalAdjacentConditions to NoIdenticalAdjacentConditions(),
      BTreeRules.controlNodeMustHaveChildren to ControlNodeMustHaveChildren(),
      BTreeRules.conditionsNotLastChild to ConditionNotLastChild(),
    )

  val nodeFactory = {
    LeafNodeRegistry
      .of()
      .nodes
      .filter { it is ActionNode<*> }
      .values
      .random(Random(DEFAULT_SEED))
  }

  val nodeRegistry = LeafNodeRegistry.of()

  val mutations =
    listOf(
      BTreeRandomAdditionTool(nodeRegistry),
      BTreeRandomDeletionTool(),
      BTreeRandomModificationTool(nodeRegistry),
    )
}
