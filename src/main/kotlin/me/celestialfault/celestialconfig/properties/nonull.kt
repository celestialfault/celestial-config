package me.celestialfault.celestialconfig.properties

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import me.celestialfault.celestialconfig.PrimitiveProperty
import me.celestialfault.celestialconfig.Property
import kotlin.reflect.KProperty

/**
 * Wrapper around [PrimitiveProperty], enforcing that the stored value cannot be null
 *
 * @see PrimitiveProperty.notNullable
 */
@Suppress("MemberVisibilityCanBePrivate")
class NoNullProperty<T> internal constructor(val wrapped: PrimitiveProperty<T>) : Property<T> {
	init {
		require(wrapped.default != null) { "Non-nullable property cannot have a null default" }
		require(wrapped.get() != null) { "Non-nullable property cannot currently have a null value" }
	}

	override val key: String get() = wrapped.key

	override fun save(): JsonElement? = wrapped.save()?.takeIf { !it.isJsonNull }
	override fun load(element: JsonElement) {
		if(element.isJsonNull) return
		if(element is JsonPrimitive && wrapped.isValid(element)) {
			wrapped.fromPrimitive(element)?.let { set(it) }
		}
	}

	/**
	 * @see PrimitiveProperty.get
	 */
	fun get(): T = wrapped.get()!!

	/**
	 * @see PrimitiveProperty.set
	 */
	fun set(value: T) = wrapped.set(value)

	/**
	 * @see PrimitiveProperty.getAndUpdate
	 */
	fun getAndUpdate(update: (T) -> T) = set(update.invoke(get()))

	override fun toString(): String = "${save()}"

	operator fun getValue(config: Any, property: KProperty<*>): T = get()
	operator fun setValue(config: Any, property: KProperty<*>, value: T) = set(value)
}
