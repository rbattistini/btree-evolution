package it.irs.evolution

import arrow.core.Either
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jenetics.Genotype
import io.jenetics.Phenotype
import io.jenetics.engine.Constraint
import io.jenetics.util.ISeq
import it.irs.simulation.Environment
import it.irs.simulation.btree.node.TreeNode
import it.irs.simulation.btree.transformation.reparation.BTreeReparation
import it.irs.simulation.btree.transformation.reparation.RepairTool
import it.irs.simulation.btree.transformation.validation.BTreeValidation.isValid
import it.irs.simulation.btree.transformation.validation.Rule

class BTreeConstraint<E>(
  private val validationRules: List<Rule>,
  toolset: Map<Rule, RepairTool>,
  nodeFactory: () -> TreeNode<E>,
  private val defaultAttempts: Int,
) : Constraint<BTreeGene<E>, Double> where E : Environment<*> {
  private val logger: KLogger = KotlinLogging.logger {}
  private val conRepair = BTreeReparation(validationRules, toolset, nodeFactory)

  override fun test(individual: Phenotype<BTreeGene<E>, Double>): Boolean {
    val r =
      individual
        .genotype()
        .gene()
        .allele()
        .isValid(validationRules)
    if (r is Either.Left) logger.debug { r.value.toString() }
    return r !is Either.Left
  }

  override fun repair(
    individual: Phenotype<BTreeGene<E>, Double>,
    generation: Long,
  ): Phenotype<BTreeGene<E>, Double> {
    val gene = individual.genotype().gene()
    val btree = gene.allele()
    logger.debug { "Repairing the tree... " }
    logger.debug { "Before: \n${btree.string}" }
    val repairedBT = conRepair.repairWithRetry(btree, defaultAttempts)
    logger.debug { "After: \n${repairedBT.string}" }
    return Phenotype.of(
      Genotype.of(BTreeChromosome(ISeq.of(gene.newInstance(repairedBT)))),
      generation,
    )
  }
}
