import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.math.BigDecimal
import java.util.*


private val log: Logger = LoggerFactory.getLogger("Main")

@Serializable
data class DataIn(val foo: String = "foooo", val bar: String, val xyz: Int? = null)

@Serializable
data class DataUt(
  val foo: String,
  val bar: String,
  val xyz: Int? = null,
  @Serializable(with = BigDecimalSerializer::class)
  val bd: BigDecimal
)

fun main() {
  val server = embeddedServer(Netty, port = 8080) {
    routing {
      install(CallLogging) {
        level = Level.INFO

      }
      install(CallId) {
//                retrieve {
//                    it.request.queryParameters["callId"]
//                }
        header("XTracy") // header in & out
        generate {
          UUID.randomUUID().toString()
        }
      }
      install(ContentNegotiation) {
        json(json = Json {
          prettyPrint = true
          ignoreUnknownKeys = true
          isLenient = true
          coerceInputValues = true

        })
      }
      install(StatusPages) {
        exception<SerializationException> { ex ->
          call.respond(HttpStatusCode.BadRequest, mapOf("OK" to "false", "error" to ex.message))
        }
        exception<InvalidPropertiesFormatException> { ex ->
          call.respond(HttpStatusCode.Conflict, mapOf("OK" to "false", "error" to ex.message))
        }
        exception<Throwable> { t ->
          log.error("Oppppps", t)
          call.respond(HttpStatusCode.InternalServerError, "Opps ${t.message}")
        }
      }

      post("json") {
        val dataIn = call.receive<DataIn>()
        call.respond(DataUt(foo = dataIn.foo, bar = dataIn.bar, xyz = dataIn.xyz, bd = BigDecimal.valueOf(123.23)))
      }
      get("bad") {
        throw InvalidPropertiesFormatException("neeej")
      }
      route("nested") {
        get {
          call.respond("nested get")
        }
        route("x") {
          get {
            call.respond("nested X get call Id: ${call.callId}")
          }
        }
      }
    }
  }
  server.start()
//  server.stop()
  log.info("Server started")
}

internal object BigDecimalSerializer : KSerializer<BigDecimal> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: BigDecimal) = encoder.encodeString(value.toString())
  override fun deserialize(decoder: Decoder): BigDecimal = BigDecimal(decoder.decodeString())
}
