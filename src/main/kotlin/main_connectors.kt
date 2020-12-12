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
    val env = applicationEngineEnvironment {
        module {
            mainModule()
        }

        // Public API
        connector {
//            host = "0.0.0.0"
            port = 8080
        }
    }

    val server = embeddedServer(Netty, env) {
        runningLimit = 1000
    }
    server.start()
    log.info("Server started")
}

fun Application.mainModule() {
    routing {
        get("/hello") {
            call.respondText("Hellp", ContentType.Text.Plain)
        }
    }
}
