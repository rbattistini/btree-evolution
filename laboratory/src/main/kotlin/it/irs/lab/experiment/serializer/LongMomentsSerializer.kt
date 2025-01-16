package it.irs.lab.experiment.serializer

import io.jenetics.stat.LongMoments
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LongMomentsSerializer : KSerializer<LongMoments> {
  @Serializable
  @SerialName("LongMoments")
  @Suppress("LongParameterList")
  private class LongMomentsSurrogate(
    val count: Long,
    val min: Long,
    val max: Long,
    val sum: Long,
    val mean: Double,
    val variance: Double,
    val skewness: Double,
    val kurtosis: Double,
  )

  override val descriptor: SerialDescriptor = LongMomentsSurrogate.serializer().descriptor

  override fun serialize(
    encoder: Encoder,
    value: LongMoments,
  ) {
    val kurtosis = if (value.kurtosis().isNaN()) 0.0 else value.kurtosis()
    val surrogate =
      LongMomentsSurrogate(
        count = value.count(),
        min = value.min(),
        max = value.max(),
        sum = value.sum(),
        mean = value.mean(),
        variance = value.variance(),
        skewness = value.skewness(),
        kurtosis = kurtosis,
      )
    encoder.encodeSerializableValue(LongMomentsSurrogate.serializer(), surrogate)
  }

  override fun deserialize(decoder: Decoder): LongMoments {
    val surrogate = decoder.decodeSerializableValue(LongMomentsSurrogate.serializer())
    return LongMoments(
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
