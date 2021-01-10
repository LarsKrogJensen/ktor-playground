package benchmark.serde

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.unsafe.UnsafeInput
import com.esotericsoftware.kryo.unsafe.UnsafeOutput
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import com.kambi.betvalidation.satellite.domain.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.objenesis.strategy.StdInstantiatorStrategy
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@State(Scope.Benchmark)
open class ExecutionPlan {

    val event = defaultTestEvent()
    val betOffer = defaultTestBetOffer()

    val kryo = Kryo().apply {
        instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
        references = false
//        referenceResolver = {}
        register(Arrays.asList<Int>()::class.java) // dont convert to kotlin function as suggested by IJ
        register(listOf<Any?>(null).javaClass)
        register(ArrayList::class.java)
        register(emptyList<Any?>().javaClass)
        register(OffsetDateTime::class.java)
        register(BigDecimal::class.java)

        // BVS classes
        register(Event::class.java)
        register(EventParticipant::class.java)
        register(EventTeamParticipant::class.java)
        register(EventGroup::class.java)
        register(ParticipantType::class.java)
        register(EventType::class.java)
        register(EventStatus::class.java)
        register(BetOffer::class.java)
        register(BetOfferType::class.java)
        register(CashoutStatus::class.java)
        register(Outcome::class.java)
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
        val obj = ProtoBuf.decodeFromByteArray<Event>(bytes)
        check(obj == plan.event)
        blackhole.consume(obj)
    }

    @ExperimentalSerializationApi
    @Benchmark
    fun betOfferProtobuf(plan: ExecutionPlan, blackhole: Blackhole) {
        val bytes = ProtoBuf.encodeToByteArray(plan.betOffer)
        val obj = ProtoBuf.decodeFromByteArray<BetOffer>(bytes)
        check(obj == plan.betOffer)
        blackhole.consume(obj)
    }

    @ExperimentalSerializationApi
    @Benchmark
    fun eventJson(plan: ExecutionPlan, blackhole: Blackhole) {
        val text = plan.js.encodeToString(plan.event)
        val obj = plan.js.decodeFromString<Event>(text)
        check(obj == plan.event)
        blackhole.consume(obj)
    }

    @ExperimentalSerializationApi
    @Benchmark
    fun betOfferJson(plan: ExecutionPlan, blackhole: Blackhole) {
        val text = plan.js.encodeToString(plan.betOffer)
        val obj = plan.js.decodeFromString<BetOffer>(text)
        check(obj == plan.betOffer)
        blackhole.consume(obj)
    }

    @Benchmark
    fun eventKryo(plan: ExecutionPlan, blackhole: Blackhole) {
        plan.output.reset()
        plan.kryo.writeObject(plan.output, plan.event)
        plan.output.flush()
        val bytes = plan.output.toBytes()

        val input = Input(bytes)
        val obj = plan.kryo.readObject(input, Event::class.java)
        check(obj == plan.event)
        blackhole.consume(obj)
    }

    @Benchmark
    fun betOfferKryo(plan: ExecutionPlan, blackhole: Blackhole) {
        plan.output.reset()
        plan.kryo.writeObject(plan.output, plan.betOffer)
        plan.output.flush()
        val bytes = plan.output.toBytes()

        val input = Input(bytes)
        val obj = plan.kryo.readObject(input, BetOffer::class.java)
        check(obj == plan.betOffer)
        blackhole.consume(obj)
    }

}

fun serializeEventKryoSize(): Int {
    val plan = ExecutionPlan()
    plan.output.reset()
    plan.kryo.writeObject(plan.output, plan.event)
    plan.output.flush()
    return plan.output.toBytes().size
}

fun serializeEventProtoSize(): Int {
    val plan = ExecutionPlan()
    return ProtoBuf.encodeToByteArray(plan.event).size
}

fun serializeEventJsonSize(): Int {
    val plan = ExecutionPlan()
    return plan.js.encodeToString(plan.event).toByteArray().size
}

fun main() {
    println("Event size proto: ${serializeEventProtoSize()}")
    println("Event size json : ${serializeEventJsonSize()}")
    println("Event size kryo : ${serializeEventKryoSize()}")

    val opt = OptionsBuilder()
        .include(SerdeBenchmark::class.java.simpleName)
        .forks(1)
        .build()

    Runner(opt).run()
//    debugKryo()
}


fun debugKryo() {
    val plan = ExecutionPlan()

    repeat(10) {
        plan.output.reset()
        plan.kryo.writeObject(plan.output, plan.event)
        plan.output.flush()
        val bytes = plan.output.toBytes()

        val input = UnsafeInput(bytes)
        val obj = plan.kryo.readObject(input, Event::class.java)
        check(obj == plan.event)
    }
}