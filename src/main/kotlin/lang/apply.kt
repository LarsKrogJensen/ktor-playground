package lang


fun main() {
  val b: Foo? = null

  var a = b ?: Foo(1, 2).apply {
    this.b = 3
  }

  val c = b ?: Foo(1, 2).also {
    it.b = 3
  }

  val d = c?.let {
    it.b
  }

  val e = c?.run {
    this.a
  }


  println(d)
}


data class Foo(var a: Int, var b: Int)
