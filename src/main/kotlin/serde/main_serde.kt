package serde

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

@Serializable
data class Person(val name: String, val age: Int)

fun main() {

  val p = Person("Lars", 29)

  val js = Json.encodeToString(p)

  val p2 = decode<Person>(js)
}

inline fun <reified T:Any> decode(str: String): T {
  val kClass: KClass<T> = T::class

  return Json.decodeFromString(str);
}
