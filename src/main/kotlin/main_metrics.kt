import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.jmx.JmxReporter
import io.ktor.application.*
import io.ktor.metrics.dropwizard.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
    val metricRegistry = MetricRegistry()
    JmxReporter.forRegistry(metricRegistry)
        .convertRatesTo(SECONDS)
        .convertDurationsTo(MILLISECONDS)
        .build()
        .start()

    val apiServer = embeddedServer(Netty, port = 8080) {
        install(DropwizardMetrics) {
            baseName = "api.ktor.calls"
            registry = metricRegistry
        }
        routing {
            get("a") {
                call.respond("a")
            }
            get("b") {
                call.respond("b")
            }
        }
    }
    apiServer.start()
    log.info("Api Server started")

    val monotorServer = embeddedServer(Netty, port = 8081) {
        // there is a conflict as jvm metrics is registerd by the ktor DropwizardMetrics
//        install(DropwizardMetrics) {
//            baseName = "monitor.ktor.calls"
//            registry = metricRegistry
//        }
        routing {
            get("c") {
                call.respond("c")
            }
            get("d") {
                call.respond("d")
            }
        }
    }
    monotorServer.start()
    log.info("Monitor Server started")


}