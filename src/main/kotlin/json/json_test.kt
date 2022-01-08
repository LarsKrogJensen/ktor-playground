import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

fun main() {
  val js = Json {
    prettyPrint = false
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
  }

  val intData = js.decodeFromString<Anyone>(""" {"data": 1}""")
  println("int:  $intData")
  val boolData = js.decodeFromString<Anyone>(""" {"data": true}""")
  println("int:  $boolData")
  val stringData = js.decodeFromString<Anyone>(""" {"data": "1"}""")
  println("string:  $stringData")
  val list = js.decodeFromString<Anyone>(""" {"data": ["a", "b"]}""")
  println("list:  $list")
}

@Serializable
data class Anyone(
  val data: JsonElement
)  {

  val asInt:Int get() = (data as JsonPrimitive).content.toInt()

}

//@Serializable
//data class Anyone2(
//  val data: Any
//)
