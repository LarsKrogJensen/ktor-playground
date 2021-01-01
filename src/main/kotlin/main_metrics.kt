import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.jmx.JmxReporter
import io.ktor.application.*
import io.ktor.metrics.dropwizard.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.dropwizard.DropwizardExports
import io.prometheus.client.exporter.common.TextFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
  val metricRegistry = MetricRegistry()
  CollectorRegistry.defaultRegistry.register(DropwizardExports(metricRegistry))
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
      get("arne") {
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
      get ("/metrics") {
        call.respondTextWriter {
          val filter = call.request.queryParameters.getAll("name[]")?.toSet() ?: setOf()
          TextFormat.write004(this, CollectorRegistry.defaultRegistry.filteredMetricFamilySamples(filter))
        }

      }
    }
  }
  monotorServer.start()
  log.info("Monitor Server started")


}
