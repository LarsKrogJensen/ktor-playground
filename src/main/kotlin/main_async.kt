import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.time.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/hello") {
                val foo = helloSoon()
                // call is same as context call is an extension method to PipelineContext delegating to context!
                context.respondText(foo, ContentType.Text.Plain)
            }
        }
    }
    server.start()
    log.info("Server started")
}

suspend fun helloSoon(): String {
    delay(Duration.ofSeconds(1))
    return "hello"
}