package flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
  val subject = MutableSharedFlow<Int>()

  val handler = CoroutineExceptionHandler { _, exception ->
    println("CoroutineExceptionHandler got $exception with suppressed ${exception.suppressed.contentToString()}")
  }
  val scope = CoroutineScope(Dispatchers.Default + handler)
  scope.launch {
    subject.asSharedFlow()
      .catch { println("Exception, ${it.message}") }
      .collect {
        println("processing $it")
        check(it != 6)
        delay(1000)
        println("done processing $it")
      }
  }

  delay(500)
//    .retry { true }
//    .shareIn()
//    .catch { println("Ouch") }
//    .retry { true }
//    .launchIn(scope)

  (0..10).forEach {
    println("Emitting $it")
    try {
      subject.emit(it)
    } catch (e: Exception) {
      println("Error emitting")
    }
  }

  delay(100000)
}
