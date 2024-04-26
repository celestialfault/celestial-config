package me.celestialfault.celestialconfig

import com.google.gson.*
import me.celestialfault.celestialconfig.properties.ListProperty
import me.celestialfault.celestialconfig.properties.MapProperty
import me.celestialfault.celestialconfig.properties.ObjectProperty
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

/**
 * Serialization interface for loading and saving data to/from JSON, utilized by more complex property types
 * such as [ListProperty] and [MapProperty].
 */
interface Serializer<T> {
	/**
	 * Turn a runtime value into a JSON element suitable for saving to disk
	 */
	fun serialize(value: T): JsonElement?

	/**
	 * Turn a JSON element from disk into a usable type for runtime
	 */
	fun deserialize(element: JsonElement): T?

	/**
	 * Built-in serialization helpers for common types
	 */
	companion object {
		@JvmStatic val defaultGson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

		/**
		* [Int] serializer
		*/
		@JvmStatic val int = object : Serializer<Int> {
			override fun serialize(value: Int): JsonElement = JsonPrimitive(value)
			override fun deserialize(element: JsonElement): Int? = if(element is JsonPrimitive && element.isNumber) element.asInt else null
		}

		/**
		 * [Long] serializer
		 */
		@JvmStatic val long = object : Serializer<Long> {
			override fun serialize(value: Long): JsonElement = JsonPrimitive(value)
			override fun deserialize(element: JsonElement): Long? = if(element is JsonPrimitive && element.isNumber) element.asLong else null
		}

		/**
		 * [Float] serializer
		 */
		@JvmStatic val float = object : Serializer<Float> {
			override fun serialize(value: Float): JsonElement = JsonPrimitive(value)
			override fun deserialize(element: JsonElement): Float? = if(element is JsonPrimitive && element.isNumber) element.asFloat else null
		}

		/**
		 * [Double] serializer
		 */
		@JvmStatic val double = object : Serializer<Double> {
			override fun serialize(value: Double): JsonElement = JsonPrimitive(value)
			override fun deserialize(element: JsonElement): Double? = if(element is JsonPrimitive && element.isNumber) element.asDouble else null
		}

		/**
		 * [Short] serializer
		 */
		@JvmStatic val short = object : Serializer<Short> {
			override fun serialize(value: Short): JsonElement = JsonPrimitive(value)
			override fun deserialize(element: JsonElement): Short? = if(element is JsonPrimitive && element.isNumber) element.asShort else null
		}

		/**
		 * [String] serializer
		 */
		@JvmStatic val string = object : Serializer<String> {
			override fun serialize(value: String): JsonElement = JsonPrimitive(value)
			override fun deserialize(element: JsonElement): String? = if(element is JsonPrimitive) element.asString else null
		}

		/**
		 * [Boolean] serializer
		 */
		@JvmStatic val boolean = object : Serializer<Boolean> {
			override fun serialize(value: Boolean): JsonElement = JsonPrimitive(value)
			override fun deserialize(element: JsonElement): Boolean? = if(element is JsonPrimitive) element.asBoolean else null
		}

		/**
		 * [Enum] serializer using a Java class
		 */
		@JvmStatic fun <T : Enum<*>> enum(enumClass: Class<T>) = object : Serializer<T> {
			private val enumValues = enumClass.enumConstants

			override fun serialize(value: T): JsonElement = JsonPrimitive(value.name)
			override fun deserialize(element: JsonElement): T = enumValues.first { it.name == element.asString }
		}

		/**
		 * [Enum] serializer using Kotlin `reified` syntax
		 */
		inline fun <reified T : Enum<*>> enum() = enum(T::class.java)

		/**
		 * [ObjectProperty] serializer using a Java class
		 *
		 * Note that the provided [ObjectProperty] class **must** have a constructor accepting a single
		 * [JsonObject] parameter.
		 *
		 * In a Kotlin class, this constructor **must** either be:
		 *
		 * - Your primary constructor, **with no additional constructors**, using the `init` block to call `load()`; OR
		 * - A separate constructor on its own (as shown below)
		 *
		 * ## Kotlin Example
		 *
		 * ```
		 * class UserData() : ObjectProperty<UserData>("") {
		 *     constructor(data: JsonObject) {
		 *         load(data)
		 *     }
		 *     
		 *     // place your variables here as normal
		 * }
		 *
		 * val users = Property.list("users", Serializer.obj<UserData>())
		 * ```
		 *
		 * ## Java Example
		 *
		 * ```java
		 * public static class UserData extends ObjectProperty<UserData> {
		 *     public UserData() {
		 *         super("");
		 *     }
		 *
		 *     public UserData(JsonObject obj) {
		 *         this();
		 *         load(obj);
		 *     }
		 *
		 *     // place your variables here as normal
		 * }
		 *
		 * public final ListProperty<UserData> users = Property.list("users", Serializer.obj(UserData::class));
		 * ```
		 */
		@JvmStatic fun <T : ObjectProperty<T>> obj(obj: Class<T>) = object : Serializer<T> {
			override fun serialize(value: T): JsonElement = value.save()
			override fun deserialize(element: JsonElement): T? = if(element is JsonObject) {
				obj.getConstructor(JsonObject::class.java).newInstance(element)
			} else null
		}

		/**
		 * [ObjectProperty] serializer using Kotlin `reified` syntax
		 */
		inline fun <reified T : ObjectProperty<T>> obj() = obj(T::class.java)

		/**
		 * [MutableMap] serializer using a second serializer for contained values
		 */
		@JvmStatic fun <T> map(serializer: Serializer<T>) = object : Serializer<MutableMap<String, T>> {
			override fun serialize(value: MutableMap<String, T>): JsonElement = JsonObject().apply {
				value.forEach {
					add(it.key, serializer.serialize(it.value))
				}
			}

			override fun deserialize(element: JsonElement): MutableMap<String, T>? {
				if(element !is JsonObject) return null
				return mutableMapOf<String, T>().apply {
					element.entrySet().forEach {
						serializer.deserialize(it.value)?.let { value -> put(it.key, value) }
					}
				}
			}
		}

		/**
		 * [MutableMap] serializer using Kotlin `reified` syntax to find a built-in [Serializer]
		 */
		inline fun <reified T> map() = map(findSerializer<T>())

		/**
		 * [MutableList] serializer using a second serializer for contained values
		 */
		@JvmStatic fun <T> list(serializer: Serializer<T>) = object : Serializer<MutableList<T>> {
			override fun serialize(value: MutableList<T>): JsonElement = JsonArray().apply {
				value.forEach {
					add(serializer.serialize(it))
				}
			}

			override fun deserialize(element: JsonElement): MutableList<T>? {
				if(element !is JsonArray) return null
				return mutableListOf<T>().apply {
					element.forEach {
						serializer.deserialize(it)?.let(::add)
					}
				}
			}
		}

		/**
		 * [MutableList] serializer using Kotlin `reified` syntax to find a built-in [Serializer]
		 */
		inline fun <reified T> list() = list(findSerializer<T>())

		/**
		 * Serializer using GSON `@Expose` annotations on an arbitrary class type, such as a `data class` or `record`
		 *
		 * Note that the default [Gson] instance will only (de)serialize fields marked with `@Expose`;
		 * you should provide your own [Gson] instance if you want behavior differently to this.
		 *
		 * The provided class **must** have a constructor with no arguments.
		 */
		@JvmOverloads
		@JvmStatic fun <T> expose(type: Class<T>, gson: Gson = defaultGson) = object : Serializer<T> {
			override fun serialize(value: T): JsonElement? = gson.toJsonTree(value)
			override fun deserialize(element: JsonElement): T? = gson.fromJson(element, type)
		}

		/**
		 * GSON `@Expose` serializer using Kotlin `reified` syntax
		 *
		 * Note that the default [Gson] instance will only (de)serialize fields marked with `@Expose`;
		 * you should provide your own [Gson] instance if you want behavior differently to this.
		 */
		inline fun <reified T> expose(gson: Gson = defaultGson) = expose(T::class.java, gson = gson)

		/**
		 * Find a [Serializer] providing type [T]
		 *
		 * Note that this will only find simple serializer types which can be represented in a `val` field
		 * (such as [int] and [string]), and not ones that require use of a function (such as [enum] and [map]).
		 *
		 * @throws NoSuchElementException if no such built-in serializer exists
		 */
		@Suppress("UNCHECKED_CAST")
		inline fun <reified T> findSerializer(): Serializer<T> =
			Companion::class.memberProperties
				.filter { it.returnType.arguments.isNotEmpty() }
				.first { it.returnType.arguments[0].type?.isSupertypeOf(T::class.starProjectedType) ?: false }
				.getter.call(Companion) as Serializer<T>
	}
}
