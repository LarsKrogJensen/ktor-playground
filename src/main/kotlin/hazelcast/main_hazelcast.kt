import com.hazelcast.config.Config
import com.hazelcast.config.InMemoryFormat
import com.hazelcast.config.MapConfig
import com.hazelcast.config.SerializerConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.map.IMap
import com.hazelcast.nio.ObjectDataInput
import com.hazelcast.nio.ObjectDataOutput
import com.hazelcast.nio.serialization.PortableReader
import com.hazelcast.nio.serialization.PortableWriter
import com.hazelcast.nio.serialization.StreamSerializer
import com.hazelcast.nio.serialization.VersionedPortable
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.future.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("Main")

fun main() {
  val config = Config().apply {
    addMapConfig(
      MapConfig()
        .setName("data")
        .setInMemoryFormat(InMemoryFormat.OBJECT)
    )
    serializationConfig.addSerializerConfig(
      SerializerConfig()
        .setImplementation(StuffSerializer)
        .setTypeClass(Stuff::class.java)
    )
    serializationConfig.addSerializerConfig(
      SerializerConfig()
        .setImplementation(MoreSerializer)
        .setTypeClass(More::class.java)
    )
  }
  val hazelcast = Hazelcast.newHazelcastInstance(config)
  val map = hazelcast.getMap<String, Stuff>("data")

  val server = embeddedServer(Netty, port = 8080) {
    install(ContentNegotiation) {
      json(json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
      })
    }
    routing {
      get("/get") {
        val id = call.request.queryParameters["id"]
        when (val stuff = map.fetch(id)) {
          null -> call.respond(status = HttpStatusCode.NotFound, message = "Opps id '$id' not found")
          else -> call.respond(stuff)
        }
      }
      post("/post") {
        val input = call.receive<Stuff>()
        val updated = map.upsert(input)
        call.respond(updated)
      }
    }
  }
  server.start()
  log.info("Server started")
}

interface Versionable {
  val version: Int
}

interface Identifiable<T> {
  val id: T
}

@Serializable
data class Stuff(
  override val id: String,
  override val version: Int,
  val b: Int,
  val more: More
) : Versionable, Identifiable<String> {

//  override fun getFactoryId() = 1
//
//  override fun getClassId() = 12
//
//  override fun writePortable(writer: PortableWriter) {
//    writer.writeUTF("id", id)
//    writer.writeInt("v", version)
//    writer.writeInt("b", b)
//  }
//
//  override fun readPortable(reader: PortableReader) {
////    id =
//  }
//
//  override fun getClassVersion(): Int = 1
}

@Serializable
data class More(val xxx: Int)

object StuffSerializer : StreamSerializer<Stuff> {
  override fun getTypeId() = 123

  override fun write(out: ObjectDataOutput, value: Stuff) {
    out.writeUTF(value.id)
    out.writeInt(value.version)
    out.writeInt(value.b)
    out.writeObject(value.more)
  }

  override fun read(input: ObjectDataInput): Stuff {
    return Stuff(
      id = input.readUTF(),
      version = input.readInt(),
      b = input.readInt(),
      more = input.readObject()
    )
  }
}

object MoreSerializer : StreamSerializer<More> {
  override fun getTypeId() = 456

  override fun write(out: ObjectDataOutput, value: More) {
    out.writeInt(value.xxx)
  }

  override fun read(input: ObjectDataInput): More {
    return More(
      xxx = input.readInt(),
    )
  }
}

suspend fun <K, V> IMap<K, V>.fetch(key: K): V? {
  return getAsync(key).await()
}

suspend fun <K, V> IMap<K, V>.upsert(value: V): Boolean where V : Versionable, V : Identifiable<K> {
  return submitToKey(value.id) { entry ->
    if (entry.value == null || value.version > entry.value.version) {
      entry.setValue(value)
      true
    } else {
      false
    }
  }.await()
}
