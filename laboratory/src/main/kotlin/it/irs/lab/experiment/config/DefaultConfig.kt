package it.irs.lab.experiment.config

import it.irs.lab.btree.GridWorldLNode
import it.irs.lab.env.GridWorld
import it.irs.lab.genetic.Exp1GenePool.ofExp1
import it.irs.simulation.btree.node.leaf.ActionNode
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.randomization.BTreeRandomAdditionTool
import it.irs.simulation.btree.transformation.randomization.BTreeRandomDeletionTool
import it.irs.simulation.btree.transformation.randomization.BTreeRandomModificationTool
import it.irs.simulation.btree.transformation.randomization.UnaryRandomTool
import it.irs.simulation.btree.transformation.reparation.ConditionNotLastChild
import it.irs.simulation.btree.transformation.reparation.ControlNodeMustHaveChildren
import it.irs.simulation.btree.transformation.reparation.NoConsecutiveControlNodes
import it.irs.simulation.btree.transformation.reparation.NoIdenticalAdjacentConditions
import it.irs.simulation.btree.transformation.reparation.RepairTool
import it.irs.simulation.btree.transformation.validation.BTreeRules
import it.irs.simulation.btree.transformation.validation.Rule
import kotlin.random.Random

object DefaultConfig {
  // Engine Parameters
  const val POP_SIZE = 30
  const val MAX_PHENOTYPE_AGE: Long = 500
  const val MUTATION_PROB = 0.5
  const val CROSSOVER_PROBABILITY = 0.6

  // Grid generation parameters
  const val DEFAULT_SEED = 42
  const val DEFAULT_DIMENSION = 7
  const val DEFAULT_NUM_OBSTACLES = 12
  const val MAX_VALIDATION_ATTEMPTS = 7
  const val DEFAULT_NEIGHBOURS_RADIUS = 1

  // Fitness Function Parameters
  const val GOAL_REACHED_REWARD = 100.0
  const val COLLISION_PENALTY = 100.0
  const val BACKTRACKING_PENALTY = 30.0
  const val TREE_COMPLEXITY_PENALTY = 60.0
  const val MAX_TREE_SIZE = 20
  const val MAX_SIM_STEPS = 100
  const val SIM_RUNS = 3

  // Evolution Stream Parameters
  const val STEADY_FITNESS = 600
  const val MAX_GENERATIONS: Long = 3

  const val TREE_MAX_DEPTH = 2
  const val TREE_MIN_CHILDREN = 2
  const val TREE_MAX_CHILDREN = 3

  const val REPARATION_ATTEMPTS = 9

  const val ELITE_COUNT = 3
  const val TOURNAMENT_SAMPLE_SIZE = 5

  const val DELTA_TIME = 1
  const val VIRTUAL_TIME: Long = 0

  val defaultRules: List<Rule> =
    listOf(
      BTreeRules.controlNodeMustHaveChildren,
      BTreeRules.noConsecutiveControlNodes,
      BTreeRules.conditionsNotLastChild,
      BTreeRules.noIdenticalAdjacentConditions,
    )

  val defaultToolset: Map<Rule, RepairTool> =
    mapOf(
      BTreeRules.noConsecutiveControlNodes to NoConsecutiveControlNodes(),
      BTreeRules.noIdenticalAdjacentConditions to NoIdenticalAdjacentConditions(),
      BTreeRules.controlNodeMustHaveChildren to ControlNodeMustHaveChildren(),
      BTreeRules.conditionsNotLastChild to ConditionNotLastChild(),
    )

  val defaultRandom = Random(DEFAULT_SEED)

  val defaultNodeRegistry = LeafNodeRegistry.ofExp1(defaultRandom)

  val createNodeFactory: (LeafNodeRegistry<GridWorld>, Random) -> () -> GridWorldLNode = {
    reg,
    rand,
    ->
    {
      reg
        .nodes
        .values
        .filter { it is ActionNode<*> }
        .random(rand)
    }
  }

  val createMutations: (LeafNodeRegistry<GridWorld>) -> List<UnaryRandomTool<GridWorld>> = { r ->
    listOf(
      BTreeRandomAdditionTool(r),
      BTreeRandomDeletionTool(),
      BTreeRandomModificationTool(r),
    )
  }
}
