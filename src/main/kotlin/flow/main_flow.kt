package flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
  val subject = MutableSharedFlow<Int>()

  (0..2).forEach { index ->
    println("Starting processor $index")
//    async {
      subject.asSharedFlow()
            .onEach {
              println("#$index processing $it")
              delay(1000)
              println("#$index don processing $it")
            }
        .launchIn(CoroutineScope(Dispatchers.Default))
//        .collect()
//    }
  }

  (0..10).forEach {
    println("Emitting $it")
    subject.emit(it)
  }

  delay(100000)
}
