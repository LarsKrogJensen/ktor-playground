package coroutines

import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

//fun main() = runBlocking<Unit> {
//
////  try {
////    doIt()
////  } catch (e: Exception) {
////    println(e)
////  }
//  val handler = CoroutineExceptionHandler { _, exception ->
//    println("CoroutineExceptionHandler got $exception with suppressed ${exception.suppressed.contentToString()}")
//  }
//  val scope = CoroutineScope(handler);
////  val job = scope.launch(handler) {
////    doIt()
////  }
////
////  job.join()
//
//  val defered = scope.async {
//    doIt();
//    "aaa"
//  }
//
//  try {
//    defered.await()
//  } catch (e: Exception) {
//    println("await execption")
//  }
//}

fun main() = runBlocking {
    val job = GlobalScope.launch { // root coroutine with launch
        println("Throwing exception from launch")
        throw IndexOutOfBoundsException() // Will be printed to the console by Thread.defaultUncaughtExceptionHandler
    }
    job.join()
    println("Joined failed job")
    val deferred = GlobalScope.async { // root coroutine with async
        println("Throwing exception from async")
        throw ArithmeticException() // Nothing is printed, relying on user to call await
    }
    try {
        deferred.await()
        println("Unreached")
    } catch (e: ArithmeticException) {
        println("Caught ArithmeticException")
    }
}

suspend fun doIt() {
  throw IllegalArgumentException()
}
