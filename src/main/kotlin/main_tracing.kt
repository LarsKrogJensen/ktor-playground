import com.kambi.kazbi.kclient.KttpClient
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.time.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
    val client = KttpClient.newClient()
        .serviceLookup { "http://localhost:8080" }
        .build()

    
    val server = embeddedServer(Netty, port = 8080) {
        install(CallLogging) {
            level = Level.INFO
            mdc("appKey") { "BVS" }
            mdc("requestId") { it.request.header("X_Request_Id") ?: UUID.randomUUID().toString() }
            mdc("flowId") { it.request.header("X_Flow_Id") ?: "-" }
            mdc("requestStartTime") {
                it.request.header("X_Request_Start_Time") ?: System.currentTimeMillis().toString()
            }
        }
        routing {
            get("/hello") {
                log.info("Handling call")
                delay(Duration.ofMillis(100))
                log.info("after delay")
                val result = client.prepareGet().path("/nested").invoke<String>()
                call.respondText(result)
            }
            get("/nested") {
                log.info("Handling nested call")
                delay(Duration.ofMillis(100))
                log.info("after nested delay")
                call.respondText("OK")
            }
        }
    }
    server.start()
    log.info("Server started")

}