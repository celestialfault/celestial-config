import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import dev.celestialfault.celestialconfig.properties.ObjectProperty
import java.nio.file.Paths
import kotlin.random.Random

enum class UserType {
	ADMIN, USER, GUEST
}

object Config : AbstractConfig(Paths.get(".", "config.json")) {
	// note: properties MUST be deferred using 'by' to properly work
	var int by Property.int("int", default = 4)
	val map by Property.map<Int>("intMap")
	val list by Property.list<Int>("intList")
	var enum by Property.enum<UserType>("type", default = UserType.GUEST)

	val nestedIntList by Property.list("nestedList", Serializer.list<Int>())

	// object properties MUST be an `object` to be properly nested within configurations
	object Inner : ObjectProperty<Inner>("inner") {
		// access with Config.Inner.string
		var string by Property.string("string", default = "abc123")
	}

	val arrayOfObjects by Property.list("objects", Serializer.obj<ArrayObject>())

	// they may, however, be a `class` to allow for use in serializers; in such cases, the key used
	// is never used, and as such an empty string is perfectly acceptable.
	// do also note the limitations noted in Serializer.obj()!
	class ArrayObject() : ObjectProperty<ArrayObject>("") {
		constructor(obj: JsonObject) : this() {
			load(obj)
		}

		var a by Property.string("a")
		var b by Property.int("b")
	}
}

fun main() {
	Config.load()
	Config.map["v${Config.map.size}"] = Random.nextInt()
	Config.arrayOfObjects.add(Config.ArrayObject().apply {
		a = "hello"
	})
	Config.nestedIntList.add(mutableListOf(1))
	Config.variables.forEach(::println)
	Config.save()
}
