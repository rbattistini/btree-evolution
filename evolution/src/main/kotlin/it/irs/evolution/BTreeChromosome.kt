package it.irs.evolution

import io.jenetics.Chromosome
import io.jenetics.util.ISeq
import it.irs.simulation.Environment

class BTreeChromosome<E>(
  private val genes: ISeq<BTreeGene<E>>,
) : Chromosome<BTreeGene<E>> where E : Environment<*> {
  override fun get(idx: Int): BTreeGene<E> = genes.get(idx)

  override fun length(): Int = genes.length()

  override fun newInstance(g: ISeq<BTreeGene<E>>) = BTreeChromosome(g)

  override fun newInstance(): Chromosome<BTreeGene<E>> = BTreeChromosome(genes)
}
