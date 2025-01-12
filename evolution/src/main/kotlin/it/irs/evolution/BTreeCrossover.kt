package it.irs.evolution

import io.jenetics.Crossover
import io.jenetics.util.MSeq
import it.irs.simulation.Environment
import it.irs.simulation.btree.transformation.randomization.BTreeCrossoverTool

class BTreeCrossover<E>(
  probability: Double,
) : Crossover<BTreeGene<E>, Double>(probability)
  where E : Environment<*> {
//  private val logger: KLogger = KotlinLogging.logger {}
  private val crossoverTool = BTreeCrossoverTool<E>()

  override fun crossover(
    individual1: MSeq<BTreeGene<E>>,
    individual2: MSeq<BTreeGene<E>>,
  ): Int {
    val numberOfChangedIndividuals = 2

    val indGene1 = individual1.first()
    val indGene2 = individual2.first()

    val tree1 = indGene1.allele()
    val tree2 = indGene2.allele()

//    logger.error { "Tree 1: \n${tree1.print}" }
//    logger.error { "Tree 2: \n${tree2.print}" }

    val (cbt1, cbt2) = crossoverTool.transform(tree1, tree2)

//    logger.error { "CTree 1: \n${cbt1.print}" }
//    logger.error { "CTree 2: \n${cbt2.print}" }

    individual1.set(0, indGene1.newInstance(cbt1))
    individual2.set(0, indGene2.newInstance(cbt2))
    return numberOfChangedIndividuals
  }
}
