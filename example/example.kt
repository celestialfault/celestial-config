import com.google.gson.JsonObject
import me.celestialfault.celestialconfig.AbstractConfig
import me.celestialfault.celestialconfig.Property
import me.celestialfault.celestialconfig.properties.SubclassProperty
import me.celestialfault.celestialconfig.Serializer
import java.nio.file.Paths
import kotlin.random.Random

enum class UserType {
	ADMIN, USER, GUEST
}

object Config : AbstractConfig(Paths.get(".", "config.json")) {
	// property fields must be both public and final (or simply 'val' in kotlin)
	// note that use of 'get() =' is not supported due to how properties are loaded
	val int = Property.int("int", default = 4)
	val map = Property.map<Int>("intMap")
	val list = Property.list<Int>("intList")
	val enum = Property.enum<UserType>("type", default = UserType.GUEST)

	val arrayOfObjects = Property.list("objects", Serializer.subclass<ArrayObject>())
	val nestedIntList = Property.list("nestedList", Serializer.list<Int>())

	// note the use of 'object'!
	// in pure java you'll have to create an instance with `public final Inner inner = new Inner();`
	object Inner : SubclassProperty<Inner>("inner") {
		// access with Config.Inner.string
		val string = Property.string("string", default = "abc123")
	}

	// this is a 'class' as opposed to the above object to allow for usage in #arrayOfObjects
	// an empty key string for this example use case is acceptable as a list has no concept of keys
	class ArrayObject() : SubclassProperty<ArrayObject>("") {
		constructor(obj: JsonObject) : this() {
			load(obj)
		}

		val a = Property.string("a")
		val b = Property.int("b")
	}
}

fun main() {
	Config.load()
	Config.map["v${Config.map.size}"] = Random.nextInt()
	Config.arrayOfObjects.add(Config.ArrayObject().apply {
		a.set("hello")
	})
	Config.nestedIntList.add(mutableListOf(1))
	Config.variables.values.forEach(::println)
	Config.save()
}
