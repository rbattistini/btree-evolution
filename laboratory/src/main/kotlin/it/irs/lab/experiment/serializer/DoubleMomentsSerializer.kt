package it.irs.lab.experiment.serializer

import io.jenetics.stat.DoubleMoments
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DoubleMomentsSerializer : KSerializer<DoubleMoments> {
  @Serializable
  @SerialName("DoubleMoments")
  @Suppress("LongParameterList")
  private class DoubleMomentsSurrogate(
    val count: Long,
    val min: Double,
    val max: Double,
    val sum: Double,
    val mean: Double,
    val variance: Double,
    val skewness: Double,
    val kurtosis: Double,
  )

  override val descriptor: SerialDescriptor = DoubleMomentsSurrogate.serializer().descriptor

  override fun serialize(
    encoder: Encoder,
    value: DoubleMoments,
  ) {
    val kurtosis = if (value.kurtosis().isNaN()) 0.0 else value.kurtosis()
    val surrogate =
      DoubleMomentsSurrogate(
        count = value.count(),
        min = value.min(),
        max = value.max(),
        sum = value.sum(),
        mean = value.mean(),
        variance = value.variance(),
        skewness = value.skewness(),
        kurtosis = kurtosis,
      )
    encoder.encodeSerializableValue(DoubleMomentsSurrogate.serializer(), surrogate)
  }

  override fun deserialize(decoder: Decoder): DoubleMoments {
    val surrogate = decoder.decodeSerializableValue(DoubleMomentsSurrogate.serializer())
    return DoubleMoments(
      surrogate.count,
      surrogate.min,
      surrogate.max,
      surrogate.sum,
      surrogate.mean,
      surrogate.variance,
      surrogate.skewness,
      surrogate.kurtosis,
    )
  }
}
