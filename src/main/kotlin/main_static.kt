import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            static("docs") {
                resources("docs")
                defaultResource("index.html")
            }
            static("other") {
                resources("other")
                defaultResource("index.html")
            }
        }
    }
    server.start()
    log.info("Server started")
}