package loadshed

import com.codahale.metrics.EWMA
import com.codahale.metrics.ExponentialMovingAverages
import com.codahale.metrics.MovingAverages
import com.codahale.metrics.SlidingTimeWindowArrayReservoir
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


enum class LoadRating(val sampling: Int) {
  Excellent(1),
  OK(2),
  Bad(10)
}

interface LoadScorer {
  fun update(durationNanos: Long)
  val rating: LoadRating
}

// Implemention inspired by gatling apdex
// https://github.com/nuxeo/gatling-report/blob/master/src/main/java/org/nuxeo/tools/gatling/report/Apdex.java
// improved with a sliding time window count
class Apdex(
  satisfiedThresold: Duration,
  tolerableThreshold: Duration
) : LoadScorer {

  private val satisfiedThresoldNanos = satisfiedThresold.toNanos()
  private val tolerableThresholdNanos = tolerableThreshold.toNanos()

  private val satisfied = SlidingTimeWindowArrayReservoir(30, TimeUnit.SECONDS)
  private val tolerated = SlidingTimeWindowArrayReservoir(30, TimeUnit.SECONDS)
  private val frustrated = SlidingTimeWindowArrayReservoir(30, TimeUnit.SECONDS)

  override fun update(durationNanos: Long) {
    if (durationNanos < satisfiedThresoldNanos) {
      satisfied.update(durationNanos)
    } else if (durationNanos < tolerableThresholdNanos) {
      tolerated.update(durationNanos)
    } else {
      frustrated.update(durationNanos)
    }
  }

  override val rating: LoadRating
    get() {
      val satisfiedCount = satisfied.size()
      val tolerableCount = tolerated.size()
      val total: Int = satisfiedCount + tolerableCount + frustrated.size()
      if (total < 10) {
        // not enough measurements to say anything
        return LoadRating.Excellent
      }

      // apdex formula:
      val scoreValue: Double = ((satisfiedCount + tolerableCount / 2) / total).toDouble()

      return when (scoreValue) {
        in 0.8..1.0 -> LoadRating.Excellent
        in 0.6..0.8 -> LoadRating.OK
        else        -> LoadRating.Bad
      }
    }
}

class EwmaScorer : LoadScorer {
  private val ewma = EWMA.oneMinuteEWMA()

  override fun update(durationNanos: Long) {
    ewma.update(durationNanos)
  }

  override val rating: LoadRating
    get() = TODO("Not yet implemented")

}


class LoadShedder<T>(
  private val scorer: LoadScorer,
  private val fallback: T
) {
  private val counter: AtomicLong = AtomicLong(0)

  suspend fun execute(block: suspend () -> T): T {
    val score = scorer.rating
    val count = counter.incrementAndGet()

    if (count % score.sampling == 0L) {
      val start = System.nanoTime()
      try {
        val result = block()
        scorer.update(System.nanoTime() - start)
        return result
      } catch (e: Exception) {
        scorer.update(Long.MAX_VALUE)
        throw e
      }
    }
    return fallback
  }
}
