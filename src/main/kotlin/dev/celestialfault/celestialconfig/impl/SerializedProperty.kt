package dev.celestialfault.celestialconfig.impl

import com.google.gson.JsonElement
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.KProperty

@Internal
class SerializedProperty<T : Any>(
	override val key: String,
	val serializer: Serializer<T>,
	default: T,
) : Property<T> {
	private var value: T = default
	override var dirty: Boolean = false

	override fun save(): JsonElement? = serializer.serialize(value)

	override fun load(element: JsonElement) {
		if(element.isJsonNull) return
		serializer.deserialize(element)?.let { this.value = it }
	}

	override fun getValue(config: Any, property: KProperty<*>): T = value
	override fun setValue(config: Any, property: KProperty<*>, value: T) {
		this.value = value
		this.dirty = true
	}

	override fun toString(): String = "${save()}"
}
