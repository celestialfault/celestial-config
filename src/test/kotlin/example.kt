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

object Config : AbstractConfig(Paths.get(".", "config.json")) {
	// note: properties MUST be deferred ('by'), otherwise they will not do anything!
	var int: Int by Property.of("int", default = 4)
	val map: MutableMap<String, Int> by Property.of("intMap", Serializer.map<Int>(), mutableMapOf())
	val list: MutableList<Int> by Property.of("intList", serializer = Serializer.list<Int>(), mutableListOf())
	var enum: UserType by Property.of("type", Serializer.enum<UserType>(), default = UserType.GUEST)

	val nestedIntList: MutableList<MutableList<Int>> by Property.of("nestedList", Serializer.list(Serializer.list<Int>()), mutableListOf())

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
	Config.load()
	Config.map["v${Config.map.size}"] = Random.nextInt()
	Config.arrayOfObjects.add(Config.ArrayObject().apply {
		a = "hello"
	})
	Config.nestedIntList.add(mutableListOf(1))
	Config.variables.forEach(::println)
	Config.save()
}
