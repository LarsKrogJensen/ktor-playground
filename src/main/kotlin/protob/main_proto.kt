package protob

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kryo.User

@Serializable
data class Project(val name: String, val language: String)

@ExperimentalUnsignedTypes
@ExperimentalSerializationApi
fun main() {
  val user = User("Mario", Address("Manchester", "Flat 86", "M###XX"), listOf())
  val bytes = ProtoBuf.encodeToByteArray(user)
  println(bytes.toAsciiHexString())
  val obj = ProtoBuf.decodeFromByteArray<protob.User>(bytes)
  println(obj)
}


@ExperimentalUnsignedTypes
fun ByteArray.toAsciiHexString() = joinToString("") {
  if (it in 32..127) it.toChar().toString() else
    "{${it.toUByte().toString(16).padStart(2, '0').toUpperCase()}}"
}


@Serializable
data class Address(
  val city: String,
  val line: String,
  val postCode: String,
  val xxx: String? = null
)
@Serializable
data class User(
  val name: String,
  val address: Address,
  val friends: List<String> = emptyList()
)
