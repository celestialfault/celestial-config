package dev.celestialfault.celestialconfig.migrations

import com.google.gson.JsonObject

/**
 * Function interface defining a migration used by [Migrations]
 *
 * Implementing methods must modify the provided [JsonObject] in-place to apply migrations
 */
fun interface Migration {
	fun migrate(json: JsonObject)
}
