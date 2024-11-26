package dev.celestialfault.celestialconfig.properties

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import kotlin.reflect.KProperty

class MapProperty<T>(override val key: String, private val serializer: Serializer<T>) : MutableMap<String, T>, Property<MutableMap<String, T>> {
	private val map: MutableMap<String, T> = mutableMapOf()

	constructor(key: String, serializer: Serializer<T>, defaults: Map<String, T>?) : this(key, serializer) {
		defaults?.let { this.map.putAll(it) }
	}

	override val entries: MutableSet<MutableMap.MutableEntry<String, T>>
		get() = this.map.entries
	override val keys: MutableSet<String>
		get() = this.map.keys
	override val size: Int
		get() = this.map.size
	override val values: MutableCollection<T>
		get() = this.map.values

	override fun clear() = this.map.clear()
	override fun isEmpty(): Boolean = this.map.isEmpty()
	override fun remove(key: String): T? = this.map.remove(key)
	override fun put(key: String, value: T): T? = this.map.put(key, value)
	override fun putAll(from: Map<out String, T>) = this.map.putAll(from)
	override fun get(key: String): T? = this.map[key]
	override fun containsValue(value: T): Boolean = this.map.containsValue(value)
	override fun containsKey(key: String): Boolean = key in this.map

	override fun save(): JsonElement = JsonObject().apply {
		this@MapProperty.forEach { (k, v) ->
			add(k, serializer.serialize(v))
		}
	}

	override fun load(element: JsonElement) {
		if(element !is JsonObject) return
		element.entrySet()
			.map { it.key to serializer.deserialize(it.value) }
			.filter { it.second != null }
			.forEach { put(it.first, it.second!!) }
	}

	override fun toString(): String {
		return "${save()}"
	}

	operator fun getValue(config: Any, prop: KProperty<*>): MutableMap<String, T> = map
}
