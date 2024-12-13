package dev.celestialfault.celestialconfig.impl

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.KProperty

@Internal
class NullableSerializedProperty<T : Any?>(
	override val key: String,
	val serializer: Serializer<T>,
	default: T?,
) : Property<T?> {
	private var value: T? = default
	override var dirty: Boolean = false

	override fun save(): JsonElement? = value.let { if(it == null) JsonNull.INSTANCE else serializer.serialize(it) }

	override fun load(element: JsonElement) {
		this.value = serializer.deserialize(element)
	}

	override fun getValue(config: Any, property: KProperty<*>): T? = value
	override fun setValue(config: Any, property: KProperty<*>, value: T?) {
		this.dirty = true
		this.value = value
	}

	override fun toString(): String = "${save()}"
}
