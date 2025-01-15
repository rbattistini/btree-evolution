package it.irs.lab.experiment.config

import io.jenetics.stat.DoubleMoments
import io.jenetics.stat.IntMoments
import io.jenetics.stat.LongMoments
import it.irs.lab.experiment.serializer.DoubleMomentsSerializer
import it.irs.lab.experiment.serializer.IntMomentsSerializer
import it.irs.lab.experiment.serializer.LongMomentsSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ExperimentResult(
  // Initial configuration
  val leafNodes: List<String> = emptyList(),
  val mutations: List<String> = emptyList(),
  val reparations: List<String> = emptyList(),
  // Evolution statistics
  val generations: Int = 0,
  @Serializable(with = IntMomentsSerializer::class)
  val altered: IntMoments? = null,
  @Serializable(with = IntMomentsSerializer::class)
  val killed: IntMoments? = null,
  @Serializable(with = IntMomentsSerializer::class)
  val invalids: IntMoments? = null,
  // Fitness statistics
  val bestBTree: String = "",
  @Serializable(with = LongMomentsSerializer::class)
  val phenotypeAge: LongMoments? = null,
  @Serializable(with = DoubleMomentsSerializer::class)
  val bestFitness: DoubleMoments? = null,
  @Serializable(with = DoubleMomentsSerializer::class)
  val alterDuration: DoubleMoments? = null,
  @Serializable(with = DoubleMomentsSerializer::class)
  val evolveDuration: DoubleMoments? = null,
  @Serializable(with = DoubleMomentsSerializer::class)
  val selectionDuration: DoubleMoments? = null,
  @Serializable(with = DoubleMomentsSerializer::class)
  val evalDuration: DoubleMoments? = null,
)
