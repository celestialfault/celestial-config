package dev.celestialfault.celestialconfig

import java.math.BigDecimal
import java.math.BigInteger

@Suppress("UNCHECKED_CAST")
fun <T : Number> coerce(value: T, min: T?, max: T?): T =
	when(value) {
		is Int -> value.coerceIn(min as Int?, max as Int?)
		is BigInteger -> value.coerceIn(min as BigInteger?, max as BigInteger?)
		is Long -> value.coerceIn(min as Long?, max as Long?)
		is Float -> value.coerceIn(min as Float?, max as Float?)
		is Double -> value.coerceIn(min as Double?, max as Double?)
		is BigDecimal -> value.coerceIn(min as BigDecimal?, max as BigDecimal?)
		is Short -> value.coerceIn(min as Short?, max as Short?)
		else -> throw IllegalArgumentException("Unrecognized number type ${value::class}")
	} as T
