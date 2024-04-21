package me.celestialfault.celestialconfig.properties

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.celestialfault.celestialconfig.Property
import me.celestialfault.celestialconfig.VariableLookup

/**
 * [Property] type used to store variables in a subclass, such as an `object`
 *
 * ## Example usage:
 *
 * ```
 * object Config : AbstractConfig(...) {
 *     object Inner : SubclassProperty<Inner>("inner") {
 *         val innerValue = Property.string("value", default = "Hello world!")
 *     }
 * }
 *
 * fun main() {
 *     Config.load()
 *     println(Config.Inner.innerValue.get())
 *     Config.Inner.innerValue.set("Hi!")
 *     Config.save()
 * }
 * ```
 */
open class SubclassProperty<T>(override val key: String) : Property<T>, VariableLookup() {
	protected val unacceptedKeys: MutableMap<String, JsonElement> = mutableMapOf()

	override fun save(): JsonElement =
		JsonObject().apply {
			variables.forEach { it.value.save()?.let { value -> add(it.key, value) } }
			unacceptedKeys
				.filter { !has(it.key) }
				.forEach { add(it.key, it.value) }
		}

	override fun load(element: JsonElement) {
		if(element !is JsonObject) return
		element.entrySet().forEach {
			variables[it.key]?.load(it.value) ?: run { unacceptedKeys.put(it.key, it.value) }
		}
	}

	override fun toString(): String {
		return "$key=${save()}"
	}
}
