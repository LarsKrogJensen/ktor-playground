package lang

import java.util.*

fun main() {
  val arne: Long? = 10

  val yyy = arne.map {
    it.toString()
  }.orElseGet {
    "sune"
  }
  val opt: Optional<String>
  println("arne = $yyy")
//  val xxx = arne.flatMap { it.toString() }
}

fun <T, R> T?.flatMap(mapper: (T) -> R?): R? = this?.let(mapper)
fun <T, R> T?.map(mapper: (T) -> R): R? = this?.let(mapper)
fun <T> T?.orElse(other: T): T = this ?: other
fun <T> T?.orElseGet(supplier: () -> T): T = this ?: supplier()
fun <T> T?.or(supplier: () -> T): T = this ?: supplier()
fun <T> T?.filter(predicate: (T) -> Boolean): T? =
  if (this != null && predicate(this)) this else null


