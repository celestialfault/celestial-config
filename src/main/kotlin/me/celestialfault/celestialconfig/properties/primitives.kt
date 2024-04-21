package me.celestialfault.celestialconfig.properties

import com.google.gson.JsonPrimitive
import me.celestialfault.celestialconfig.PrimitiveProperty

abstract class StringLikeProperty<T> protected constructor(key: String, default: T? = null) : PrimitiveProperty<T>(key, default) {
	override fun toPrimitive(value: T): JsonPrimitive? = JsonPrimitive(value as String)
	override fun isValid(primitive: JsonPrimitive): Boolean = primitive.isString
}

class StringProperty(key: String, default: String? = null) : StringLikeProperty<String>(key, default) {
	override fun fromPrimitive(primitive: JsonPrimitive): String = primitive.asString
}

class CharProperty(key: String, default: Char? = null) : StringLikeProperty<Char>(key, default) {
	override fun isValid(primitive: JsonPrimitive): Boolean = super.isValid(primitive) && primitive.asString.isNotEmpty()
	override fun fromPrimitive(primitive: JsonPrimitive): Char = primitive.asString.toCharArray()[0]
}

class BooleanProperty(key: String, default: Boolean? = null) : PrimitiveProperty<Boolean>(key, default) {
	override fun toPrimitive(value: Boolean): JsonPrimitive = JsonPrimitive(value)
	override fun isValid(primitive: JsonPrimitive): Boolean = primitive.isBoolean
	override fun fromPrimitive(primitive: JsonPrimitive): Boolean = primitive.asBoolean
}
