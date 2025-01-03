import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import dev.celestialfault.celestialconfig.ObjectProperty
import java.nio.file.Paths
import kotlin.random.Random

enum class UserType {
	ADMIN, USER, GUEST
}

object ExampleConfig : AbstractConfig(Paths.get(".", "tests", "config.json")) {
	// note: properties MUST be deferred ('by'), otherwise they will not do anything!
	var int: Int by Property.of("int", default = 4)
	val map: MutableMap<String, Int> by Property.of("intMap", Serializer.map<Int>(), mutableMapOf())
	val list: MutableList<Int> by Property.of("intList", serializer = Serializer.list<Int>(), mutableListOf())
	var enum: UserType by Property.of("type", Serializer.enum<UserType>(), default = UserType.GUEST)

	val nestedIntList: MutableList<MutableList<Int>> by Property.of("nestedList", Serializer.list(Serializer.list<Int>()), mutableListOf())

	var coerced by Property.of("coerced", Serializer.number(max = 100), 1)

	// object properties MUST be an `object` to be properly nested within configurations
	object Inner : ObjectProperty<Inner>("inner") {
		// access with Config.Inner.string
		var string by Property.of<String>("string", default = "abc123")
	}

	val arrayOfObjects: MutableList<ArrayObject> by Property.of("objects", Serializer.list(Serializer.obj<ArrayObject>()), mutableListOf())

	// they may, however, be a `class` to allow for use in serializers; in such cases, the key used
	// is never used, and as such an empty string is perfectly acceptable.
	// do also note the limitations noted in Serializer.obj()!
	class ArrayObject() : ObjectProperty<ArrayObject>("") {
		constructor(obj: JsonObject) : this() {
			load(obj)
		}

		var a: String? by Property.ofNullable("a")
		var b: Int? by Property.ofNullable("b")
	}
}

fun main() {
	ExampleConfig.load()
	ExampleConfig.map["v${ExampleConfig.map.size}"] = Random.nextInt()
	ExampleConfig.arrayOfObjects.add(ExampleConfig.ArrayObject().apply {
		a = "hello"
	})
	ExampleConfig.nestedIntList.add(mutableListOf(1))
	ExampleConfig.variables.forEach(::println)
	ExampleConfig.save()
}
