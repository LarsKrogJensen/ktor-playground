import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.Serializable


import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val log: Logger = LoggerFactory.getLogger("Main")

@Serializable
data class DataIn(val foo: String, val bar: String)
@Serializable
data class DataUt(val foo: String, val bar: String)

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            install(ContentNegotiation) {
                json()
            }
            post("/json") {
                val dataIn = call.receive<DataIn>()
                call.respond(DataUt(foo = dataIn.foo, bar = dataIn.bar))
            }
        }
    }
    server.start()
    log.info("Server started")
}