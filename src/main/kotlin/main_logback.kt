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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val loggger: Logger = LoggerFactory.getLogger("se.lars.Main")

@KtorExperimentalLocationsAPI
fun main() {
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
      get("/hello") {
        loggger.info("Saying hello, debug enabled: ${loggger.isDebugEnabled}")
        call.respondText("Hello", ContentType.Text.Plain)
      }
      get("/logger/{logger}/level") {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val logger = loggerContext.getLogger(call.parameters["logger"])
        call.respondText("Logger ${logger.name}, Level: ${logger.level}", ContentType.Text.Plain)
      }
//      post<LoggerConf> { logConf ->
//        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
//        val logger = loggerContext.getLogger(logConf.logger)
//        logger.level = Level.toLevel(logConf.level)
//        call.respondText("Logger ${logger.name}, Level: ${logger.level}", ContentType.Text.Plain)
//      }
    }
  }
  server.start()
  loggger.info("Server started")
}


@KtorExperimentalLocationsAPI
@Location("/logger/{logger}/level/{level}")
data class LoggerConf(val logger: String, val level: String)
