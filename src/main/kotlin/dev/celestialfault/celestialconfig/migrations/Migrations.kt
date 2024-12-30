package dev.celestialfault.celestialconfig.migrations

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.jetbrains.annotations.ApiStatus.Internal

/**
 * Simple migration utility, applied to the loaded [JsonObject] before being loaded by the underlying properties
 *
 * **Note:** Removing migrations is **not** supported, and will result in existing configurations no longer loading
 * when [apply] is called; you should instead opt to make any such migrations you need to remove a noop
 *
 * @see Migration
 */
class Migrations private constructor(private val migrations: MutableMap<Int, Migration>) {

	/**
	 * Returns the current highest migration version known by this [Migrations] instance
	 */
	val currentVersion: Int get() = migrations.keys.maxOrNull() ?: 0

	/**
	 * Applies all applicable [Migration]s to the provided [JsonObject]
	 *
	 * @see Migration
	 * @see VERSION_KEY
	 */
	@Internal
	fun apply(json: JsonObject) {
		val version = json.get(VERSION_KEY)?.takeIf { it is JsonPrimitive && it.isNumber }?.asInt ?: 0

		require(version !in 0..currentVersion) { "Unrecognized config version!" }
		if(version == currentVersion) return

		val toRun = migrations.filter { it.key > version }.toSortedMap()

		toRun.forEach { version, migration ->
			migration.migrate(json)
			json.addProperty(VERSION_KEY, version)
		}
	}

	/**
	 * Migration builder provided by [create]
	 */
	class Builder @Internal constructor() {
		private val migrations = mutableMapOf<Int, Migration>()

		/**
		 * Register a new [Migration] to apply
		 *
		 * @param id        Optional version ID; this must be unique. Defaults to `max() + 1` if omitted
		 * @param migration The [Migration] to run
		 */
		fun add(id: Int = ((migrations.keys.maxOrNull() ?: 0) + 1), migration: Migration) {
			migrations.put(id, migration)
		}

		@Internal
		fun build(): Migrations {
			return Migrations(migrations)
		}
	}

	companion object {
		/**
		 * The key used in configuration files for storing the current migration version
		 *
		 * The value stored under this key (if any) **must** be a number within the range 0 .. [Migrations.currentVersion]
		 */
		const val VERSION_KEY = "configVersion"

		/**
		 * Create a new [Migrations] instance
		 *
		 * ## Example
		 *
		 * ```kt
		 * val migrations = Migrations.create {
		 *     // simple property rename
		 *     add { it.add("new", it.remove("old")) }
		 *
		 *     // anything you can accomplish by modifying the underlying JsonObject you can do with migrations
		 *     add { it.addProperty("theAnswerToLife", 42) }
		 *
		 *     // note that you are working entirely outside the strict typing safety properties provide,
		 *     // and as such you should ensure to be careful with any changes you make - properties will
		 *     // silently discard anything that isn't of the correct type
		 * }
		 *
		 * object Config : AbstractConfig(..., migrations = migrations) { ... }
		 * ```
		 */
		inline fun create(builder: Builder.() -> Unit): Migrations = Builder().apply(builder).build()
	}
}
