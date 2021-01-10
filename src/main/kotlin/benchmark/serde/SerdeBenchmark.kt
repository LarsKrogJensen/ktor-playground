package benchmark.serde

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.objenesis.strategy.StdInstantiatorStrategy
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.util.concurrent.TimeUnit


@State(Scope.Benchmark)
open class ExecutionPlan {

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

  val js = Json {
    prettyPrint = false
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
  }
}


@Fork(value = 1, warmups = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
open class SerdeBenchmark {

  @ExperimentalSerializationApi
  @Benchmark
  fun eventProtobuf(plan: ExecutionPlan, blackhole: Blackhole) {
    val bytes = ProtoBuf.encodeToByteArray(plan.event)
    val obj = ProtoBuf.decodeFromByteArray<TestEvent>(bytes)
    check(obj == plan.event)
    blackhole.consume(obj)
  }

  @ExperimentalSerializationApi
  @Benchmark
  fun eventJson(plan: ExecutionPlan, blackhole: Blackhole) {
    val bytes = plan.js.encodeToString(plan.event)
    val obj = plan.js.decodeFromString<TestEvent>(bytes)
    check(obj == plan.event)
    blackhole.consume(obj)
  }

  @Benchmark
  fun eventKryo(plan: ExecutionPlan, blackhole: Blackhole) {
    plan.output.reset()
    plan.kryo.writeObject(plan.output, plan.event)
    plan.output.flush()
    val bytes = plan.output.toBytes()


    val input = Input(bytes)
    val obj = plan.kryo.readObject(input, TestEvent::class.java)
    check(obj == plan.event)
    blackhole.consume(obj)
  }

}

fun main() {
  val opt = OptionsBuilder()
    .include(SerdeBenchmark::class.java.simpleName)
    .forks(1)
    .build()

  Runner(opt).run()
}


@Serializable
data class TestAdress(
  val city: String,
  val line: String,
  val postCode: String,
  val xxx: String? = null
)

@Serializable
data class TestEvent(
  val name: String,
  val address: TestAdress,
  val friends: List<String> = emptyList()
)
