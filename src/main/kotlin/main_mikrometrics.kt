import io.ktor.application.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
    val meterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    meterRegistry.config()

        .commonTags("arne", "anka", "pelle", "fant")
        .meterFilter(PrefixFilter())

    ClassLoaderMetrics().bindTo(meterRegistry)
    JvmMemoryMetrics().bindTo(meterRegistry)
    JvmGcMetrics().bindTo(meterRegistry)
    ProcessorMetrics().bindTo(meterRegistry)
    JvmThreadMetrics().bindTo(meterRegistry)

    val apiServer = embeddedServer(Netty, port = 8080) {
        install(MicrometerMetrics) {
            baseName = "external.api"
            registry = meterRegistry
            meterBinders = listOf()
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

    val monitorServer = embeddedServer(Netty, port = 8081) {
        install(MicrometerMetrics) {
            baseName = "monitor.api"
            registry = meterRegistry
            meterBinders = listOf()
            distributionStatisticConfig = DistributionStatisticConfig.builder()
                .percentilesHistogram(true)
                .percentiles()
                .build()

        }
        routing {
            get("/metrics") {
                call.respond(meterRegistry.scrape())
            }
        }
    }
    monitorServer.start()
    log.info("Monitor Server started")
}

class PrefixFilter : MeterFilter {
    override fun map(id: Meter.Id): Meter.Id {
        return id.withName("bvs.${id.name}")
    }
}