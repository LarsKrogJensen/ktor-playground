import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore


private val log: Logger = LoggerFactory.getLogger("Main")

// run with curl -v  --noproxy "*" --insecure --http2 https://localhost:8081/hello
fun main() {
    val keyStoreFile = File("test.jks")
    val keyStore = KeyStore.getInstance(keyStoreFile, "devhead".toCharArray())

    val env = applicationEngineEnvironment {
        module {
            routing {
                get("/hello") {
                    call.respondText("Hellp", ContentType.Text.Plain)
                }
            }
        }
        connector {
            host = "0.0.0.0"
            port = 8080
        }
        sslConnector(keyStore = keyStore, keyAlias = "testkey", keyStorePassword = { "devhead".toCharArray() }, privateKeyPassword = { "devhead".toCharArray() }) {
            port = 8081
            keyStorePath = keyStoreFile.absoluteFile
        }
    }

    val server = embeddedServer(Netty, env) {
        runningLimit = 1000

    }
    server.start()
    log.info("Server started")
}

