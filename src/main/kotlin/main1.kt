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
      get("/hello") {
        call.respondText("Hellp", ContentType.Text.Plain)
      }
    }
    environment.monitor.subscribe(ApplicationStarting) { event ->
      log.info("starting: $event")
    }
    environment.monitor.subscribe(ApplicationStarted) { event ->
      log.info("started: $event")
    }
    environment.monitor.subscribe(ApplicationStopPreparing) { event ->
      log.info("stop preparing: $event")
    }
    environment.monitor.subscribe(ApplicationStopping) { event ->
      log.info("stopping: $event")
    }
    environment.monitor.subscribe(ApplicationStopped) { event ->
      log.info("stopped: $event")
    }
  }
  Runtime.getRuntime().addShutdownHook(Thread {
    log.info("Shutting down")
    server.stop(gracePeriod = 5, timeout = 30, timeUnit = TimeUnit.SECONDS)
    log.info("Server down")
  })
  server.start(true)
}
