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
        module()
    }.start(false)
    Runtime.getRuntime().addShutdownHook(Thread {
        log.info("Server closing down gracefully")
        server.stop(30, 30, TimeUnit.SECONDS)
        log.info("Server stopped")
    })

    log.info("Server started")
    
}

fun Application.module() {
    environment.monitor.subscribe(ApplicationStarted) {
        println("My app is ready to roll")
    }
    environment.monitor.subscribe(ApplicationStopped) {
        println("Time to clean up")
    }
    routing {
        get("/delay") {
            log.info("Delaying...")
            delay(Duration.ofSeconds(20))
            log.info("Delaying done")
            call.respondText("Hellp", ContentType.Text.Plain)
        }
    }
}