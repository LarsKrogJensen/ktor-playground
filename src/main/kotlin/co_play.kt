import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

private val log: Logger = LoggerFactory.getLogger("Main")

fun main() = runBlocking(Dispatchers.Default) {
//    sample1()
    //sample2()
//    sample3()
//    sample4()
//    sample5()
//    val time = measureTimeMillis {
//        log.info("The answer is ${failedConcurrentSum()}")
//    }
//    val xx = async {
//        Thread.sleep(1000)
//        log.info("sleep done")
//    }
//    xx.await()
//    log.info("Completed")
}

suspend fun failedConcurrentSum(): Int = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE) // Emulates very long computation
            42
        } finally {
            println("First child was cancelled")
        }
    }
    val two = async<Int> {
        println("Second child throws an exception")
        throw ArithmeticException()
    }
    one.await() + two.await()
}

private suspend fun CoroutineScope.sample5() {
    val millis = measureTimeMillis {
        val foo = async { doFoo() }
        val bar = async { doBar() }
        println("result = ${foo.await() + bar.await()}")
    }

    println("millis = $millis")
}

private suspend fun CoroutineScope.sample4() {
    val job = launch {
        repeat(1000) { i ->
            log.info("job: I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // delay a bit
    log.info("main: I'm tired of waiting!")
    job.cancel() // cancels the job
    job.join() // waits for job's completion
    log.info("main: Now I can quit.")
}

suspend fun doFoo(): Int {
    log.info("foo")
    delay(1000)
    return 15
}

suspend fun doBar(): Int {
    log.info("bar")
    delay(1000)
    return 27
}

suspend fun fooBar(): Int = coroutineScope {
    val foo = async { doFoo() }
    val bar = async { doBar() }

    foo.await() + bar.await()
}

private fun CoroutineScope.sample3() {
    repeat(100_000) { // launch a lot of coroutines
        launch {
            delay(5000L)
            log.info(".")
        }
    }
}

private suspend fun CoroutineScope.sample2() {
    launch {
        delay(200L)
        log.info("Task from runBlocking")
    }

    coroutineScope { // Creates a coroutine scope
        launch {
            delay(500L)
            log.info("Task from nested launch")
        }

        delay(100L)
        log.info("Task from coroutine scope") // This line will be printed before the nested launch
    }

    log.info("Coroutine scope is over") // This line is not printed until the nested launch completes
}

private fun CoroutineScope.sample1() {
    launch { // launch a new coroutine in the scope of runBlocking
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}