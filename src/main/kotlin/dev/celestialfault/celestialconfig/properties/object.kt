package dev.celestialfault.celestialconfig.properties

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.VariableLookup

/**
 * [Property] superclass type used to store variables in a JSON object with differing types
 *
 * ## Example usage:
 *
 * ```
 * object Config : AbstractConfig(...) {
 *     object Inner : ObjectProperty<Inner>("inner") {
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
open class ObjectProperty<T>(override val key: String) : Property<T>, VariableLookup() {
	/**
	 * @see dev.celestialfault.celestialconfig.AbstractConfig.unacceptedKeys
	 */
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
		return "${save()}"
	}
}
