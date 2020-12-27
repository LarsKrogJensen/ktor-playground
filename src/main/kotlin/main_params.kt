import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/params/{id}/x") {
              call.respondText("Hello path param ${call.parameters["id"]} arg ${call.request.queryParameters["y"]}", ContentType.Text.Plain)
            }
        }
    }
    server.start()
    log.info("Server started")
}
