package it.irs.evolution

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jenetics.Mutator
import it.irs.simulation.Environment
import it.irs.simulation.btree.transformation.randomization.UnaryRandomTool
import java.util.random.RandomGenerator

class BTreeMutator<E>(
  probability: Double,
  private val mutations: List<UnaryRandomTool<E>>,
) : Mutator<BTreeGene<E>, Double>(probability) where E : Environment<*> {
  private val logger: KLogger = KotlinLogging.logger {}

  override fun mutate(
    gene: BTreeGene<E>,
    random: RandomGenerator,
  ): BTreeGene<E> = if (gene.isValid) this.mutate0(gene, random) else gene

  private fun mutate0(
    gene: BTreeGene<E>,
    random: RandomGenerator,
  ): BTreeGene<E> {
    val mutator = mutations[random.nextInt(mutations.size)]
    val tree = gene.allele()
    logger.trace { "Mutator: $mutator" }
    logger.trace { "Before: \n${tree.string}" }
    val mutatedGene = mutator.transform(tree)
    logger.trace { "After: \n${mutatedGene.string}" }
    return gene.newInstance(mutatedGene)
  }
}
