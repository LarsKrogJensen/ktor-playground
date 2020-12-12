import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        intercept(ApplicationCallPipeline.Call) {
            println("interceptor 1")
        }
        routing {
            intercept(ApplicationCallPipeline.Call) {
                println("interceptor 2")
            }
            route("/hello") {
                intercept(ApplicationCallPipeline.Call) {
                    println("interceptor 3")
                }
                get {
                    println("hello")
                    call.respondText("Hellp", ContentType.Text.Plain)
                }

            }
            route("/world") {
                get {
                    println("World")
                    call.respond("World")
                }
            }
        }
    }
    server.start()
    log.info("Server started")
}
