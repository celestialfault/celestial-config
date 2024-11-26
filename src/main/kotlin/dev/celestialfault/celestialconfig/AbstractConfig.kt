package dev.celestialfault.celestialconfig

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createParentDirectories

/**
 * Simple wrapper around GSON for loading configuration files.
 *
 * ## Example usage:
 *
 * ```
 * val path = Paths.of(".", "config.json")
 *
 * enum class UserType {
 *     ADMIN, USER, GUEST
 * }
 *
 * object MyConfig : AbstractConfig(path) {
 *     var intKey by Property.int(key = "int", default = 0)
 *
 *     object UserData : ObjectProperty<UserData>("user_data") {
 *         var enumKey by Property.enum<UserType>("type", default = UserType.USER)
 *     }
 * }
 *
 * fun main() {
 *     println(MyConfig.intKey) // 0
 *     MyConfig.intKey = 1
 *     println(MyConfig.intKey) // 1
 *     MyConfig.save()
 *     // subsequent .load() calls will result in the first intKey read being 1
 * }
 * ```
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractConfig protected constructor(
	/**
	 * The path at which this configuration file will load and save its contents
	 */
	protected val path: Path,
	/**
	 * If `true`, [load] will instead call [save] if the provided [path] doesn't exist already
	 */
	protected val createIfMissing: Boolean = true,
) : VariableLookup() {

	/**
	 * Controls the indentation of the saved file
	 *
	 * @see JsonWriter.setIndent
	 */
	protected var indent: String = "\t"

	/**
	 * A store of keys which were not accepted by any variables when loading the configuration file from disk
	 */
	protected val unacceptedKeys: MutableMap<String, JsonElement> = mutableMapOf()

	/**
	 * Create a new [AbstractConfig] with a config file path
	 *
	 * @apiNote This constructor will implicitly set [.createIfMissing] to `true`
	 *
	 * @param path The path to where your config file should reside on disk
	 */
	protected constructor(path: Path) : this(path, true)

	/**
	 * Load the configuration file saved on disk into memory, or create a new one if it doesn't exist (and [createIfMissing]
	 * was not set to `false`)
	 */
	@Throws(IOException::class)
	fun load() {
		val configFile = path.toFile()
		if(!configFile.exists()) {
			if(createIfMissing) {
				save()
			}
			return
		}

		FileReader(configFile).use { reader ->
			val data = ADAPTER.fromJson(reader)
			data.entrySet().forEach { entry: Map.Entry<String, JsonElement> ->
				val k = entry.key
				val v = entry.value
				variables[k]?.load(v) ?: run { unacceptedKeys.put(k, v) }
			}
		}
	}

	/**
	 * Save all variables to the configuration file on disk
	 */
	@Throws(IOException::class)
	fun save() {
		val configFile = path.toFile()
		path.toAbsolutePath().createParentDirectories()
		FileWriter(configFile).use { writer ->
			JsonWriter(writer).use { jsonWriter ->
				jsonWriter.setIndent(indent)
				val obj = JsonObject()
				variables.forEach { (name: String, variable: Property<*>) ->
					variable.save()?.let { obj.add(name, it) }
				}
				unacceptedKeys.entries
					.filter { !obj.has(it.key) }
					.forEach { obj.add(it.key, it.value) }
				ADAPTER.write(jsonWriter, obj)
			}
		}
	}

	private companion object {
		private val ADAPTER: TypeAdapter<JsonObject> = Gson().getAdapter(JsonObject::class.java)
	}
}
