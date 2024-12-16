package dev.celestialfault.celestialconfig

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import dev.celestialfault.celestialconfig.impl.NullableSerializedProperty
import dev.celestialfault.celestialconfig.impl.SerializedProperty
import kotlin.reflect.KProperty

/**
 * Basic property type, supporting the bare minimum needed to load and save to/from disk
 */
interface Property<T> {
	/**
	 * Flag to determine if this property has changes that haven't been saved yet
	 */
	var dirty: Boolean

	/**
	 * The key that this property is stored as in the generated JSON
	 */
	val key: String

	/**
	 * Write the current value of this [Property] to a [JsonElement] to save
	 *
	 * This property won't be encoded in the saved JSON file if `null` is returned; you should instead
	 * return [JsonNull] if you want a `null` value to be saved for this property
	 */
	fun save(): JsonElement?

	/**
	 * Load the provided [JsonElement] from disk into the current
	 */
	fun load(element: JsonElement)

	/**
	 * Kotlin operator function used to read the property's value from a deferred property
	 *
	 * This **must** be implemented, as [AbstractConfig] only supports deferred properties
	 */
	operator fun getValue(config: Any, property: KProperty<*>): T

	/**
	 * Kotlin operator function used to set the property's value from a deferred property
	 *
	 * This **may** be implemented, or throw [UnsupportedOperationException], depending on the
	 * use case of the property
	 */
	operator fun setValue(config: Any, property: KProperty<*>, value: T)

	/**
	 * Utility methods for creating properties
	 */
	@Suppress("unused")
	companion object {
		/**
		 * Create a [Property] for the given type [T], automatically finding an appropriate [Serializer]
		 *
		 * @see Serializer.findSerializer
		 */
		inline fun <reified T : Any> of(key: String, default: T): Property<T> {
			return SerializedProperty(key, Serializer.findSerializer(), default)
		}

		/**
		 * Create a [Property] for the given nullable type [T], automatically finding an appropriate [Serializer]
		 *
		 * @see Serializer.findSerializer
		 */
		inline fun <reified T : Any?> ofNullable(key: String, default: T? = null): Property<T?> {
			return NullableSerializedProperty(key, Serializer.findSerializer(), default)
		}

		/**
		 * Create a [Property] for the given type [T] with the provided serializer
		 */
		fun <T : Any> of(key: String, serializer: Serializer<T>, default: T): Property<T> {
			return SerializedProperty(key, serializer, default)
		}

		/**
		 * Create a [Property] for the given nullable type [T] with the provided serializer
		 */
		fun <T : Any?> ofNullable(key: String, serializer: Serializer<T>, default: T? = null): Property<T?> {
			return NullableSerializedProperty(key, serializer, default)
		}
	}
}
