import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import jdk.jfr.Configuration
import jdk.jfr.Recording
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Duration


private val loggger: Logger = LoggerFactory.getLogger("se.lars.Main")

@KtorExperimentalLocationsAPI
fun main() {
  val recordings: MutableMap<Long, Recording> = mutableMapOf()

  val server = embeddedServer(Netty, port = 8080) {
    install(Locations)
    install(ContentNegotiation) {
      json(json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true

      })
    }
    routing {
      get("/start") {
        val recording = Recording(Configuration.getConfiguration("profile")).apply {
          destination = File.createTempFile("ktor-${id}-", ".jfr").toPath();
          duration = Duration.ofMinutes(1)
          name = "ktor-test"
          recordings[id] = this
          start()
        }

        call.respondText("Recording ${recording.id} started: ${recording.destination}", ContentType.Text.Plain)
      }
      get("/status/{id}") {
        val recording = recordings[call.parameters["id"]?.toLong()]
        call.respondText("Recording status ${recording?.state}")
      }
      get("/content/{id}") {
        val recording = recordings[call.parameters["id"]?.toLong()]
        if (recording != null) {
          call.respondFile(recording.destination.toFile())
        } else {
          call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
      }
    }
  }
  server.start()
  loggger.info("Server started")
}

