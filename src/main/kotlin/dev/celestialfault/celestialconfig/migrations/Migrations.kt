package dev.celestialfault.celestialconfig.migrations

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.jetbrains.annotations.ApiStatus.Internal

/**
 * Migrations applied to the loaded [JsonObject] before being applied to the underlying properties
 */
class Migrations private constructor(private val migrations: MutableMap<Int, Migration>) {

	/**
	 * Returns the current highest migration version known by this [Migrations] instance
	 */
	val currentVersion: Int get() = migrations.keys.maxOrNull() ?: 0

	@Internal
	fun apply(json: JsonObject) {
		val version = json.get(VERSION_KEY)?.takeIf { it is JsonPrimitive && it.isNumber }?.asInt ?: 0

		check(version <= currentVersion) { "Unrecognized config version!" }
		if(version == currentVersion) return

		val toRun = migrations.filter { it.key > version }.toSortedMap()

		toRun.forEach { version, migration ->
			migration.migrate(json)
			json.addProperty(VERSION_KEY, version)
		}
	}

	class Builder @Internal constructor() {
		private val migrations = mutableMapOf<Int, Migration>()

		fun add(id: Int? = null, migration: Migration) {
			migrations.put(id ?: ((migrations.keys.maxOrNull() ?: 0) + 1), migration)
		}

		@Internal
		fun build(): Migrations {
			return Migrations(migrations)
		}
	}

	companion object {
		const val VERSION_KEY = "configVersion"

		/**
		 * Create a new [Migrations] instance
		 *
		 * ## Example
		 *
		 * ```kt
		 * val migrations = Migrations.create {
		 *     add { it.add("new", it.remove("old")) } // simple property rename
		 *     // anything you can accomplish by modifying the underlying JsonObject you can do with migrations
		 * }
		 *
		 * object Config : AbstractConfig(..., migrations = migrations) { ... }
		 * ```
		 */
		inline fun create(builder: Builder.() -> Unit): Migrations = Builder().apply(builder).build()
	}
}
