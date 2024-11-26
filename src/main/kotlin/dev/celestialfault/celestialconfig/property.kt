package dev.celestialfault.celestialconfig

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import dev.celestialfault.celestialconfig.properties.BooleanProperty
import dev.celestialfault.celestialconfig.properties.CharProperty
import dev.celestialfault.celestialconfig.properties.DoubleProperty
import dev.celestialfault.celestialconfig.properties.EnumProperty
import dev.celestialfault.celestialconfig.properties.FloatProperty
import dev.celestialfault.celestialconfig.properties.IntegerProperty
import dev.celestialfault.celestialconfig.properties.ListProperty
import dev.celestialfault.celestialconfig.properties.LongProperty
import dev.celestialfault.celestialconfig.properties.MapProperty
import dev.celestialfault.celestialconfig.properties.NoNullProperty
import dev.celestialfault.celestialconfig.properties.ShortProperty
import dev.celestialfault.celestialconfig.properties.StringProperty
import kotlin.reflect.KProperty

/**
 * Basic property type, supporting the bare minimum needed to load and save to/from disk
 */
interface Property<T> {
	/**
	 * The key that this property is stored as in the generated JSON
	 */
	val key: String

	/**
	 * Write the current value of this [Property] to a [JsonElement] to save.
	 *
	 * This property won't be encoded in the saved JSON file if `null` is returned; you should instead
	 * return [JsonNull] if you want a `null` value to be saved for this property.
	 */
	fun save(): JsonElement?

	/**
	 * Load the provided [JsonElement] from disk into the current
	 */
	fun load(element: JsonElement)

	/**
	 * Utility methods for getting commonly stored property types
	 */
	@Suppress("unused")
	companion object {
		/**
		 * Create a new [String] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 */
		fun string(key: String, default: String? = null) =
			StringProperty(key, default)

		/**
		 * Create a new [Boolean] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 */
		fun boolean(key: String, default: Boolean? = null) =
			BooleanProperty(key, default)

		/**
		 * Create a new [Char] [Property] type
		 *
		 * Note that while [Char] values are saved as strings in the encoded JSON, only the first character
		 * of the loaded string will be used when loading from JSON.
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 */
		fun char(key: String, default: Char? = null) =
			CharProperty(key, default)

		/**
		 * Create a new [Int] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		fun int(key: String, default: Int? = null, min: Int? = null, max: Int? = null) =
			IntegerProperty(key, default, min, max)

		/**
		 * Create a new [Long] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		fun long(key: String, default: Long? = null, min: Long? = null, max: Long? = null) =
			LongProperty(key, default, min, max)

		/**
		 * Create a new [Short] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		fun short(key: String, default: Short? = null, min: Short? = null, max: Short? = null) =
			ShortProperty(key, default, min, max)

		/**
		 * Create a new [Float] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		fun float(key: String, default: Float? = null, min: Float? = null, max: Float? = null) =
			FloatProperty(key, default, min, max)

		/**
		 * Create a new [Double] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		fun double(key: String, default: Double? = null, min: Double? = null, max: Double? = null) =
			DoubleProperty(key, default, min, max)

		/**
		 * Create a new [Enum] [Property] type
		 *
		 * ## Example usage:
		 *
		 * ```
		 * val property = Property.enum<UserType>(key = "type", default = UserType.GUEST)
		 * ```
		 */
		inline fun <reified T : Enum<*>> enum(key: String, default: T? = null, saveAsOrdinal: Boolean = false): EnumProperty<T> =
			EnumProperty(key, default, T::class.java, saveAsOrdinal)

		/**
		 * Create a new [MutableMap] [Property] type
		 */
		fun <T> map(key: String, serializer: Serializer<T>, defaults: Map<String, T>? = null): MapProperty<T> =
			MapProperty(key, serializer, defaults)

		/**
		 * Create a new [MutableMap] [Property] type, using a built-in serializer from [Serializer]
		 *
		 * ## Example usage:
		 *
		 * ```
		 * val property = Property.map<Int>("ints")
		 * ```
		 */
		inline fun <reified T> map(key: String, defaults: Map<String, T>? = null): MapProperty<T> =
			MapProperty(key, Serializer.findSerializer<T>(), defaults)

		/**
		 * Create a new [MutableList] [Property] type
		 */
		fun <T> list(key: String, serializer: Serializer<T>, defaults: List<T>? = null): ListProperty<T> =
			ListProperty(key, serializer, defaults)

		/**
		 * Create a new [MutableList] [Property] type, using a built-in serializer from [Serializer]
		 *
		 * ## Example usage:
		 *
		 * ```
		 * val property = Property.list<Int>("ints")
		 * ```
		 */
		inline fun <reified T> list(key: String, defaults: List<T>? = null): ListProperty<T> =
			ListProperty(key, Serializer.findSerializer<T>(), defaults)
	}
}

/**
 * Basic property type for values that don't require special handling to store, such as [Int] and [String]
 */
abstract class PrimitiveProperty<T> protected constructor(override val key: String, val default: T?) : Property<T> {
	protected open var value: T? = default

	/**
	 * Get the current stored value in this property
	 */
	open fun get(): T? = this.value

	/**
	 * Set the value to be stored in this property
	 */
	open fun set(value: T?) {
		this.value = value
	}

	/**
	 * A utility method combining both [get] and [set]
	 */
	open fun getAndUpdate(update: (T?) -> T?) = set(update.invoke(get()))

	/**
	 * Encode the provided [value] in a [JsonElement] to be stored on disk
	 */
	abstract fun toPrimitive(value: T): JsonPrimitive?

	/**
	 * Check if the provided [primitive] is valid for this property
	 *
	 * If this returns `false`, the primitive is ignored entirely.
	 */
	abstract fun isValid(primitive: JsonPrimitive): Boolean

	/**
	 * Load the provided [primitive] into a value able to be stored
	 */
	abstract fun fromPrimitive(primitive: JsonPrimitive): T?

	override fun save(): JsonElement? = value?.let { toPrimitive(it) } ?: JsonNull.INSTANCE
	override fun load(element: JsonElement) {
		if(element.isJsonNull) {
			set(null)
			return
		}
		if(element is JsonPrimitive && isValid(element)) {
			set(fromPrimitive(element))
		}
	}

	/**
	 * Wrap the current [PrimitiveProperty] in a [dev.celestialfault.celestialconfig.properties.NoNullProperty], enforcing that the stored value cannot be null
	 */
	fun notNullable() = NoNullProperty(this)

	override fun toString(): String {
		return "${save()}"
	}

	/** @suppress */
	operator fun getValue(config: Any, property: KProperty<*>): T? = get()
	/** @suppress */
	operator fun setValue(config: Any, property: KProperty<*>, value: T?) = set(value)
}
