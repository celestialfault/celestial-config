package me.celestialfault.celestialconfig.properties

import com.google.gson.JsonPrimitive
import me.celestialfault.celestialconfig.PrimitiveProperty

class EnumProperty<T : Enum<*>>(key: String, default: T?, private val enumClass: Class<T>) : PrimitiveProperty<T>(key, default) {
	private val enumValues: Array<T> get() = enumClass.enumConstants

	override fun toPrimitive(value: T): JsonPrimitive = JsonPrimitive(value.name)

	override fun isValid(primitive: JsonPrimitive): Boolean =
		(primitive.isNumber && primitive.asNumber.toInt().let { ordinal -> ordinal in enumValues.indices })
			|| (primitive.isString && primitive.asString.let { name -> enumValues.any { it.name == name } })

	override fun fromPrimitive(primitive: JsonPrimitive): T? {
		if(primitive.isNumber) {
			return enumValues[primitive.asNumber.toInt()]
		} else if(primitive.isString) {
			return primitive.asString.let { name -> enumValues.firstOrNull { it.name == name } }
		}
		return null
	}
}
