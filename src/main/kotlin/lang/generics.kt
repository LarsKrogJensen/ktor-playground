package lang

fun main() {
//  var list: MutableList<out Number> = mutableListOf<Int>(1,2,3,4)
//  list.forEach { num ->
//
//  }

  val closable: AutoCloseable? = null;

  closable.closeSilent()
}



fun AutoCloseable?.closeSilent() {
  try {
    this?.close()
  } catch (_: Exception) {
  }
}
