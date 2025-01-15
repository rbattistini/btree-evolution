package it.irs.evolution

import io.jenetics.Chromosome
import io.jenetics.util.ISeq
import it.irs.simulation.Environment
import it.irs.simulation.btree.builder.BTreeRandomGenerator
import it.irs.simulation.btree.node.leaf.LeafNodeRegistry
import it.irs.simulation.btree.transformation.validation.Rule

class BTreeChromosome<E>(
  private val genes: ISeq<BTreeGene<E>>,
  private val nodeRegistry: LeafNodeRegistry<E>,
  private val validationRules: List<Rule>,
  private val btreeRndGen: BTreeRandomGenerator<E>,
) : Chromosome<BTreeGene<E>> where E : Environment<*> {
  override fun get(idx: Int): BTreeGene<E> = genes.get(idx)

  override fun length(): Int = genes.length()

  override fun newInstance(g: ISeq<BTreeGene<E>>) =
    BTreeChromosome(g, nodeRegistry, validationRules, btreeRndGen)

  override fun newInstance(): Chromosome<BTreeGene<E>> {
    var genes = ISeq.empty<BTreeGene<E>>()
    for (idx in 0..genes.length()) {
      genes =
        genes.append(
          BTreeGene(btreeRndGen.randomTree(), nodeRegistry, validationRules, btreeRndGen),
        )
    }
    return BTreeChromosome(genes, nodeRegistry, validationRules, btreeRndGen)
  }
}
