package forkjoin

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.ThreadLocalRandom

fun main() {
  val count = 10_000
  val latch = CountDownLatch(count)
  val commonPool = ForkJoinPool.commonPool()
  println("Parallism: ${commonPool.parallelism} pool size: ${commonPool.poolSize} async: ${commonPool.asyncMode}")
  repeat(1000) {
    println("${Thread.currentThread().name} starting task $it")
    commonPool.execute(MyTask(it))

    println("${Thread.currentThread().name} done task $it")
    latch.countDown()
  }

  while (latch.count > 0) {
    println("${Thread.currentThread().name}  remainning ${latch.count} active threads ${commonPool.activeThreadCount} running threads ${commonPool.runningThreadCount} queued tasks ${commonPool.queuedTaskCount} steal count ${commonPool.stealCount} submissions queue ${commonPool.queuedSubmissionCount}")
    Thread.sleep(1000)
  }

  println("DOne")
//  commonPool.awaitTermination()
}

class MyTask(private val count: Int) : ForkJoinTask<Unit>() ,ForkJoinPool.ManagedBlocker{
  override fun getRawResult() {
  }

  override fun setRawResult(value: Unit?) {
  }

  override fun exec(): Boolean {
    println("${Thread.currentThread().name} running task $count")
    val waiting = ThreadLocalRandom.current().nextLong(500, 1000)

    getPool()
    Thread.sleep(waiting)
    //      val xxx = System.currentTimeMillis() + waiting
    //      while (System.currentTimeMillis() < xxx) {
    //
    //      }
    return true
  }

  override fun block(): Boolean {
    return true
  }

  override fun isReleasable(): Boolean {
    return true
  }

}
