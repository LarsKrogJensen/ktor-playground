import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/hello") {
                call.respond("hello")
            }

        }
    }
    server.start()
    log.info("Server started")
}