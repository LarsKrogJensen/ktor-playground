package lang

import java.math.BigDecimal
import java.util.HashMap

fun main() {
  val pair = 1 to "one"

  val (x, y) = pair
  println("pair = $pair")

  val (a1, b1) = Yoo(b = "asasas")
  val (i, i1) = Go(1, 2)

  val mapOf = mapOf("a" to "a", "b" to "B")
  mapOf.forEach { (t, u) -> println("T = $t U = $u") }

  val str: String? = null

  println("str = ${str?.length ?: 1}")

  var yoo = Yoo().apply {
    println("a = $a")
  }

  yoo = Yoo().also {
    println("a = ${it.a}")
  }

  var xx = yoo.let {
    ""
  }

  var xx1 = yoo.run {
    ""
  }

//  val value = xx.takeIf { it == "" }?.toBigDecimal() ?: BigDecimal.valueOf(1)

  "".count()

  val mapOf1 = mapOf(1 to 2, 3 to 4)
  val mutableMapOf = mutableMapOf(1 to 2)
  val javaMap: Map<Int, Int> = HashMap()


  println("Map type ${mapOf1.javaClass}")

  mapOf1.getOrElse(1) {

  }
}

fun String.count() = this.length

fun <T> T.myAply(block: T.() -> Unit): T {
  block()
  return this
}

fun <T> T.myAlos(block: (T) -> Unit): T {
  block(this)
  return this
}


data class Yoo(
  val a: Int = 1,
  val b: String = "ssss"
)

class Go(private val a: Int, private val b: Int) {
  operator fun component1() = a
  operator fun component2() = b
}
