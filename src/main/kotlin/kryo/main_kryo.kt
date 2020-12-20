package kryo

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import java.io.FileInputStream
import java.io.FileOutputStream
import org.objenesis.strategy.StdInstantiatorStrategy




fun main() {
  val kryo = Kryo()
  kryo.instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
  kryo.register(User::class.java)
  kryo.register(Address::class.java)

  val output = Output(FileOutputStream("file.bin"))
  val user = User("Mario", Address("Manchester", "Flat 86", "M###XX"))
  kryo.writeObject(output, user)
  output.close()

  val input = Input(FileInputStream("file.bin"))
  val u = kryo.readObject(input, User::class.java)
  input.close()
  println("u = $u")
}


data class Address(
  val city: String,
  val line: String,
  val postCode: String,
  val xxx: String? = null
)

data class User(
  val name: String,
  val address: Address,
)
