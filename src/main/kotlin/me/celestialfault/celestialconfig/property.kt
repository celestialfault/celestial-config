package me.celestialfault.celestialconfig

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import me.celestialfault.celestialconfig.properties.*

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
	companion object {
		/**
		 * Create a new [String] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 */
		@JvmStatic fun string(key: String, default: String? = null) =
			StringProperty(key, default)

		/**
		 * Create a new [Boolean] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 */
		@JvmStatic fun boolean(key: String, default: Boolean? = null) =
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
		@JvmStatic fun char(key: String, default: Char? = null) =
			CharProperty(key, default)

		/**
		 * Create a new [Int] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		@JvmStatic fun int(key: String, default: Int? = null, min: Int? = null, max: Int? = null) =
			IntegerProperty(key, default, min, max)

		/**
		 * Create a new [Long] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		@JvmStatic fun long(key: String, default: Long? = null, min: Long? = null, max: Long? = null) =
			LongProperty(key, default, min, max)

		/**
		 * Create a new [Short] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		@JvmStatic fun short(key: String, default: Short? = null, min: Short? = null, max: Short? = null) =
			ShortProperty(key, default, min, max)

		/**
		 * Create a new [Float] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		@JvmStatic fun float(key: String, default: Float? = null, min: Float? = null, max: Float? = null) =
			FloatProperty(key, default, min, max)

		/**
		 * Create a new [Double] [Property] type
		 *
		 * @param key	  The key that this property should be saved in the encoded JSON as
		 * @param default The default value for this property, defaults to null
		 * @param min     The minimum (inclusive) value for this property
		 * @param max     The maximum (inclusive) value for this property
		 */
		@JvmStatic fun double(key: String, default: Double? = null, min: Double? = null, max: Double? = null) =
			DoubleProperty(key, default, min, max)

		/**
		 * Create a new [Enum] [Property] type, using a provided [Serializer]
		 *
		 * ## Example usage:
		 *
		 * ```java
		 * public final Property<UserType> type = Property.enum("type", UserType.class, UserType.GUEST)
		 * ```
		 */
		@JvmStatic fun <T : Enum<*>> enum(key: String, enumClass: Class<T>, default: T? = null): EnumProperty<T> =
			EnumProperty(key = key, enumClass = enumClass, default = default)

		/**
		 * Create a new [Enum] [Property] type
		 *
		 * ## Example usage:
		 *
		 * ```
		 * val property = Property.enum<UserType>(key = "type", default = UserType.GUEST)
		 * ```
		 */
		inline fun <reified T : Enum<*>> enum(key: String, default: T? = null): EnumProperty<T> =
			enum(key, T::class.java, default)

		/**
		 * Create a new [MutableMap] [Property] type
		 */
		@JvmStatic fun <T> map(key: String, serializer: Serializer<T>, defaults: Map<String, T>? = null): MapProperty<T> =
			MapProperty(key, serializer, defaults = defaults)

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
			MapProperty(key = key, serializer = Serializer.findSerializer<T>(), defaults = defaults)

		/**
		 * Create a new [MutableList] [Property] type
		 */
		@JvmStatic fun <T> list(key: String, serializer: Serializer<T>, defaults: List<T>? = null): ListProperty<T> =
			ListProperty(key, serializer, defaults = defaults)

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
			ListProperty(key = key, serializer = Serializer.findSerializer<T>(), defaults = defaults)
	}
}

/**
 * Basic property type for values that can be stored with a [JsonPrimitive]
 */
abstract class PrimitiveProperty<T>(override val key: String, default: T?) : Property<T> {
	protected open var value: T? = default

	open fun get(): T? = this.value
	open fun set(value: T?) {
		this.value = value
	}

	protected abstract fun toPrimitive(value: T): JsonPrimitive?
	protected abstract fun isValid(primitive: JsonPrimitive): Boolean
	protected abstract fun fromPrimitive(primitive: JsonPrimitive): T?

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

	override fun toString(): String {
		return "$key=${save()}"
	}
}
