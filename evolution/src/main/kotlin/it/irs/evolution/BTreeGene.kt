package it.irs.evolution

import arrow.core.Either
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jenetics.Gene
import it.irs.simulation.Environment
import it.irs.simulation.btree.BehaviorTree
import it.irs.simulation.btree.builder.BTreeRandomGenerator
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.validation.BTreeValidation.isValid
import it.irs.simulation.btree.transformation.validation.Rule

class BTreeGene<E>(
  private val btree: BehaviorTree<E>,
  private val nodeRegistry: LeafNodeRegistry<E>,
  private val validationRules: List<Rule>,
  private val btreeRndGen: BTreeRandomGenerator<E>,
) : Gene<BehaviorTree<E>, BTreeGene<E>> where E : Environment<*> {
  private val logger: KLogger = KotlinLogging.logger {}

  override fun newInstance(): BTreeGene<E> =
    BTreeGene(btreeRndGen.randomTree(), nodeRegistry, validationRules, btreeRndGen)

  override fun newInstance(p0: BehaviorTree<E>): BTreeGene<E> {
    logger.trace { "New instance \n${p0.string}" }
    return BTreeGene(p0, nodeRegistry, validationRules, btreeRndGen)
  }

  override fun isValid(): Boolean =
    when (val res = btree.isValid(validationRules)) {
      is Either.Right -> res.value
      is Either.Left -> false
    }

  override fun allele(): BehaviorTree<E> = btree
}
