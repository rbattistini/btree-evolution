package it.irs.lab.experiment.serializer

import io.jenetics.stat.IntMoments
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IntMomentsSerializer : KSerializer<IntMoments> {
  @Serializable
  @SerialName("IntMoments")
  private class IntMomentsSurrogate(
    val count: Long,
    val min: Int,
    val max: Int,
    val sum: Long,
    val mean: Double,
    val variance: Double,
    val skewness: Double,
    val kurtosis: Double,
  )

  override val descriptor: SerialDescriptor = IntMomentsSurrogate.serializer().descriptor

  override fun serialize(
    encoder: Encoder,
    value: IntMoments,
  ) {
    val kurtosis = if (value.kurtosis().isNaN()) 0.0 else value.kurtosis()
    val surrogate =
      IntMomentsSurrogate(
        count = value.count(),
        min = value.min(),
        max = value.max(),
        sum = value.sum(),
        mean = value.mean(),
        variance = value.variance(),
        skewness = value.skewness(),
        kurtosis = kurtosis,
      )
    encoder.encodeSerializableValue(IntMomentsSurrogate.serializer(), surrogate)
  }

  override fun deserialize(decoder: Decoder): IntMoments {
    val surrogate = decoder.decodeSerializableValue(IntMomentsSurrogate.serializer())
    return IntMoments(
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
