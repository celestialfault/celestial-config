package dev.celestialfault.celestialconfig

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlin.reflect.KProperty

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
abstract class ObjectProperty<T> protected constructor(override val key: String) : Property<T>, VariableLookup() {
	final override var dirty: Boolean
		get() = walkProperties().any { it.dirty }
		set(_) {}

	/**
	 * @see AbstractConfig.unacceptedKeys
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

	@Suppress("UNCHECKED_CAST")
	final override fun getValue(config: Any, property: KProperty<*>): T = this as T
	final override fun setValue(config: Any, property: KProperty<*>, value: T) = throw UnsupportedOperationException()

	override fun toString(): String {
		return "${save()}"
	}
}
