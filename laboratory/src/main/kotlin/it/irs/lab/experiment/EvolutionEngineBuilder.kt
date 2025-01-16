package it.irs.lab.experiment

import io.jenetics.Optimize
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import it.irs.evolution.BTreeCrossover
import it.irs.evolution.BTreeGene
import it.irs.evolution.BTreeMutator
import it.irs.lab.ExperimentRunnerUtils.constraint
import it.irs.lab.ExperimentRunnerUtils.genotype
import it.irs.lab.ExperimentRunnerUtils.resolveRegistry
import it.irs.lab.env.GridWorld
import it.irs.lab.experiment.config.DefaultConfig.createMutations
import it.irs.lab.experiment.config.ExperimentConfig
import it.irs.lab.genetic.FitnessComputer

class EvolutionEngineBuilder(
  private val cfg: ExperimentConfig,
) {
  fun builder(): Engine.Builder<BTreeGene<GridWorld>, Double> {
    val registry = resolveRegistry(cfg)
    val genotype = genotype(registry, cfg)
    val fitnessComputer = FitnessComputer(cfg)
    val constraint = constraint(registry, cfg)
    val mutations = createMutations(registry)

    return Engine
      .builder(fitnessComputer.ff, genotype)
      .populationSize(cfg.populationSize)
      .optimize(Optimize.MAXIMUM)
      .maximalPhenotypeAge(cfg.maxPhenotypeAge)
      .constraint(constraint)
      .alterers(
        BTreeMutator(cfg.mutationProbability, mutations),
        BTreeCrossover(cfg.crossoverProbability),
      ).interceptor(EvolutionResult.toUniquePopulation())
  }
}
