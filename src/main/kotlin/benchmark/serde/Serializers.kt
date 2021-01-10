package benchmark.serde
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kazbi.BigDecimalSerializer", PrimitiveKind.DOUBLE)
    override fun serialize(encoder: Encoder, value: BigDecimal) = encoder.encodeDouble(value.toDouble())
    override fun deserialize(decoder: Decoder): BigDecimal = BigDecimal.valueOf(decoder.decodeDouble())
}

object CurrencySerializer : KSerializer<Currency> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kazbi.CurrencySerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Currency) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): Currency = Currency.getInstance(decoder.decodeString())
}

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kazbi.OffsetDateTimeSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: OffsetDateTime) = encoder.encodeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    override fun deserialize(decoder: Decoder): OffsetDateTime {
        val value = decoder.decodeString()
        if (value.contains('T')) {
            return OffsetDateTime.parse(value)
        }
        val instant = Instant.ofEpochMilli(value.toLong())
        return OffsetDateTime.ofInstant(instant, ZoneId.of("UTC"))
    }
}

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kazbi.LocalDateSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.format(DateTimeFormatter.ISO_DATE))
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kazbi.LocalTimeSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalTime) = encoder.encodeString(value.format(DateTimeFormatter.ISO_TIME))
    override fun deserialize(decoder: Decoder): LocalTime = LocalTime.parse(decoder.decodeString())
}
