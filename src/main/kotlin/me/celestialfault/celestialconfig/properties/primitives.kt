package me.celestialfault.celestialconfig.properties

import com.google.gson.JsonPrimitive
import me.celestialfault.celestialconfig.PrimitiveProperty

class StringProperty(key: String, default: String? = null) : PrimitiveProperty<String>(key, default) {
	override fun toPrimitive(value: String): JsonPrimitive = JsonPrimitive(value)
	override fun isValid(primitive: JsonPrimitive): Boolean = primitive.isString
	override fun fromPrimitive(primitive: JsonPrimitive): String = primitive.asString
}

class CharProperty(key: String, default: Char? = null) : PrimitiveProperty<Char>(key, default) {
	override fun toPrimitive(value: Char): JsonPrimitive = JsonPrimitive(value.toString())
	override fun isValid(primitive: JsonPrimitive): Boolean = primitive.isString && primitive.asString.isNotEmpty()
	override fun fromPrimitive(primitive: JsonPrimitive): Char = primitive.asString.toCharArray()[0]
}

class BooleanProperty(key: String, default: Boolean? = null) : PrimitiveProperty<Boolean>(key, default) {
	override fun toPrimitive(value: Boolean): JsonPrimitive = JsonPrimitive(value)
	override fun isValid(primitive: JsonPrimitive): Boolean = primitive.isBoolean
	override fun fromPrimitive(primitive: JsonPrimitive): Boolean = primitive.asBoolean
}
