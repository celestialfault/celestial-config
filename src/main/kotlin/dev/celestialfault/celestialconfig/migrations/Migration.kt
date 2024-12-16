package dev.celestialfault.celestialconfig.migrations

import com.google.gson.JsonObject

fun interface Migration {
	fun migrate(json: JsonObject)
}
