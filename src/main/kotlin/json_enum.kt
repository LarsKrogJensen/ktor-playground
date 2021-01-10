import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

fun main() {
    val data = Data(TestEnum.OK)

    println("data json = ${Json.encodeToString(data)}")
    println("raw enum json = ${Json.encodeToString(TestEnum.KO)}")

    val data2 = Json.decodeFromString<Data>("""{"value":"ok"}""")
    println("data with lower text = $data2")

    val data3 = Json.decodeFromString<Data>("""{"value":"gurka"}""")
    println("data with unknown text = $data3")
}

enum class TestEnum { OK, KO, DEFAULT }

@Serializable
data class Data(
    @Serializable(with = TestEnumSerializer::class)
    val value: TestEnum
)


internal object TestEnumSerializer : KSerializer<TestEnum> {

    // generated values for quick mapping
    private val values: Map<String, TestEnum> = mapOf(
        "OK" to TestEnum.OK,
        "KO" to TestEnum.KO,
    )
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TestEnum", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: TestEnum) = encoder.encodeString(value.name.toLowerCase())
    override fun deserialize(decoder: Decoder): TestEnum = values[decoder.decodeString().toUpperCase()]
        ?: TestEnum.DEFAULT
}