package benchmark.serde

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.objenesis.strategy.StdInstantiatorStrategy
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.NANOSECONDS
import kotlin.system.measureNanoTime

@ExperimentalSerializationApi
object NaiveBenchmark {

  val event = TestEvent(
    name = "Truls",
    address = TestAdress(
      city = "Stockholm",
      line = "arne",
      postCode = "jsjsjsj",
      xxx = "asasasas"
    )
  )
  val kryo = Kryo().apply {
    instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
    register(TestEvent::class.java)
    register(TestAdress::class.java)
    register(ArrayList::class.java)
    register(listOf<Int>()::class.java)
  }

  val output = Output(16 * 1024)


  fun eventProto(iterations: Int, consumer: (Any) -> Unit) {
    repeat(iterations) {
      val bytes = ProtoBuf.encodeToByteArray(event)
      //      println("proto size: ${bytes.size}")
      val obj = ProtoBuf.decodeFromByteArray<protob.User>(bytes)
      consumer(obj)
    }
  }

  fun eventKryo(iterations: Int, consumer: (Any) -> Unit) {
    repeat(iterations) {
      output.reset()
      kryo.writeObject(output, event)
      output.flush()
      val bytes = output.toBytes()

      //      println("kryo size: ${bytes.size}")

      val input = Input(bytes)
      val obj = kryo.readObject(input, TestEvent::class.java)
      consumer(obj)
    }
  }
}

@ExperimentalSerializationApi
fun main() {
  val iterations = 1000
  NaiveBenchmark.eventKryo(50) {}
  val kryoNanos = measureNanoTime {
    NaiveBenchmark.eventKryo(iterations) {}
  }

  NaiveBenchmark.eventProto(50) {}
  val protoNanos = measureNanoTime {
    NaiveBenchmark.eventProto(iterations) {}
  }

  println("Proto time per call ${NANOSECONDS.toMicros(protoNanos)/iterations.toDouble()} us")
  println("Kryo  time per call ${NANOSECONDS.toMicros(kryoNanos)/iterations.toDouble()} us")
}

