package dev.celestialfault.celestialconfig.properties

import com.google.gson.JsonPrimitive
import dev.celestialfault.celestialconfig.PrimitiveProperty

/**
 * Basic property type for all [Number]-based values, such as integers
 */
abstract class NumberProperty<T : Number> protected constructor(
	key: String, default: T?,
	val min: Comparable<T>? = null,
	val max: Comparable<T>? = null,
) : PrimitiveProperty<T>(key, default) {
	override fun isValid(primitive: JsonPrimitive): Boolean = primitive.isNumber

	override fun set(value: T?) {
		if(value != null && !validate(value)) {
			return
		}
		super.set(value)
	}

	override fun getAndUpdate(update: (T?) -> T?) {
		val value = update.invoke(get())
		if(value != null && !validate(value)) return
		set(value)
	}

	open fun validate(value: T): Boolean =
		min?.let { it <= value } ?: true
		&& max?.let { it >= value } ?: true
}

class IntegerProperty(key: String, default: Int? = null, min: Int? = null, max: Int? = null) : NumberProperty<Int>(key, default, min, max) {
	override fun toPrimitive(value: Int): JsonPrimitive = JsonPrimitive(value)
	override fun fromPrimitive(primitive: JsonPrimitive): Int = primitive.asInt
}

class FloatProperty(key: String, default: Float? = null, min: Float? = null, max: Float? = null) : NumberProperty<Float>(key, default, min, max) {
	override fun toPrimitive(value: Float): JsonPrimitive = JsonPrimitive(value)
	override fun fromPrimitive(primitive: JsonPrimitive): Float = primitive.asFloat
}

class DoubleProperty(key: String, default: Double? = null, min: Double? = null, max: Double? = null) : NumberProperty<Double>(key, default, min, max) {
	override fun toPrimitive(value: Double): JsonPrimitive = JsonPrimitive(value)
	override fun fromPrimitive(primitive: JsonPrimitive): Double = primitive.asDouble
}

class LongProperty(key: String, default: Long? = null, min: Long? = null, max: Long? = null) : NumberProperty<Long>(key, default, min, max) {
	override fun toPrimitive(value: Long): JsonPrimitive = JsonPrimitive(value)
	override fun fromPrimitive(primitive: JsonPrimitive): Long = primitive.asLong
}

class ShortProperty(key: String, default: Short? = null, min: Short? = null, max: Short? = null) : NumberProperty<Short>(key, default, min, max) {
	override fun toPrimitive(value: Short): JsonPrimitive = JsonPrimitive(value)
	override fun fromPrimitive(primitive: JsonPrimitive): Short = primitive.asShort
}
