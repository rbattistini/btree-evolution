package it.irs.lab

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jenetics.EliteSelector
import io.jenetics.Genotype
import io.jenetics.Optimize
import io.jenetics.TournamentSelector
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import io.jenetics.engine.Limits.bySteadyFitness
import io.jenetics.util.ISeq
import it.irs.evolution.BTreeChromosome
import it.irs.evolution.BTreeConstraint
import it.irs.evolution.BTreeCrossover
import it.irs.evolution.BTreeGene
import it.irs.evolution.BTreeMutator
import it.irs.lab.ExperimentConfig.CROSSOVER_PROBABILITY
import it.irs.lab.ExperimentConfig.DEFAULT_SEED
import it.irs.lab.ExperimentConfig.ELITE_COUNT
import it.irs.lab.ExperimentConfig.GENE_MAX_CHILDREN
import it.irs.lab.ExperimentConfig.GENE_MAX_DEPTH
import it.irs.lab.ExperimentConfig.GENE_MIN_CHILDREN
import it.irs.lab.ExperimentConfig.MAX_EPISODES
import it.irs.lab.ExperimentConfig.MAX_PHENOTYPE_AGE
import it.irs.lab.ExperimentConfig.MUTATION_PROB
import it.irs.lab.ExperimentConfig.POP_SIZE
import it.irs.lab.ExperimentConfig.REPARATION_ATTEMPTS
import it.irs.lab.ExperimentConfig.STEADY_FITNESS
import it.irs.lab.ExperimentConfig.TOURNAMENT_SAMPLE_SIZE
import it.irs.lab.ExperimentConfig.mutations
import it.irs.lab.ExperimentConfig.nodeFactory
import it.irs.lab.ExperimentConfig.nodeRegistry
import it.irs.lab.ExperimentConfig.rules
import it.irs.lab.ExperimentConfig.toolset
import it.irs.lab.sim.FitnessComputer
import it.irs.simulation.btree.builder.BTreeRandomGenerator
import kotlin.random.Random

// TODO make a command line which accepts parameters
// TODO make a bash script to run experiments and store data as csv
// TODO make a kotlin notebook to plot the experiment's results
fun main() {
  val logger: KLogger = KotlinLogging.logger {}
  val rndTreeGen =
    BTreeRandomGenerator(
      nodeRegistry,
      GENE_MAX_DEPTH,
      GENE_MIN_CHILDREN,
      GENE_MAX_CHILDREN,
      random = Random(DEFAULT_SEED),
    )
  val startGene =
    BTreeGene(
      rndTreeGen.randomTree(),
      nodeRegistry,
      rules,
    )
  val chr = BTreeChromosome(ISeq.of(startGene))
  val gtf = Genotype.of(chr)

  val ff = FitnessComputer()
  val con = BTreeConstraint(rules, toolset, nodeFactory, REPARATION_ATTEMPTS)
  val engine =
    Engine
      .builder(ff.ff, gtf)
      .populationSize(POP_SIZE)
      .optimize(Optimize.MAXIMUM)
      .maximalPhenotypeAge(MAX_PHENOTYPE_AGE)
      .constraint(con)
      .alterers(
        BTreeMutator(MUTATION_PROB, mutations),
        BTreeCrossover(CROSSOVER_PROBABILITY),
      ).survivorsSelector(EliteSelector(ELITE_COUNT))
      .offspringSelector(TournamentSelector(TOURNAMENT_SAMPLE_SIZE))
      .build()

  val statistics: EvolutionStatistics<Double, *> = EvolutionStatistics.ofNumber()

  val best =
    engine
      .stream()
      .limit(bySteadyFitness(STEADY_FITNESS))
      .limit(MAX_EPISODES)
      .peek(statistics)
      .peek {
        logger.error {
          "Generation: ${it.generation()}, " +
            "Best fitness ${it.bestFitness()}, " +
            "Time ${it.durations().evolveDuration}"
        }
      }.collect(EvolutionResult.toBestGenotype())

  best.forEach {
    println(it.gene().allele().string)
    println(it)
    println(statistics)
  }

//  RandomRegistry.using(RandomGeneratorFactory.getDefault()) {}
}
