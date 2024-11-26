package dev.celestialfault.celestialconfig

import dev.celestialfault.celestialconfig.properties.ObjectProperty
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * @suppress Not intended to be part of the public API
 */
@Internal
abstract class VariableLookup protected constructor() {
	@get:Internal
	val variables: Map<String, Property<*>> by lazy {
		buildMap {
			putAll(delegated)
			putAll(objects)
		}
	}

	private val delegated by lazy {
		this@VariableLookup::class.memberProperties
			.asSequence()
			.map { getDelegate(it, this) }
			.filterNotNull()
			.associateBy { it.key }
	}

	private val objects by lazy {
		this@VariableLookup::class.nestedClasses
			.asSequence()
			.filter { it.visibility == KVisibility.PUBLIC }
			.filter { it.objectInstance != null }
			.filter { it.isSubclassOf(ObjectProperty::class) }
			.map { it.objectInstance as ObjectProperty<*> }
			.onEach { require(it.key.isNotEmpty()) { "Object key cannot be an empty string" } }
			.associateBy { it.key }
	}

	@Suppress("UNCHECKED_CAST")
	private fun getDelegate(prop: KProperty1<out VariableLookup, *>, it: Any): Property<*>? =
		// delegates are weird
		(prop as KProperty1<Any, *>).apply { isAccessible = true }.getDelegate(it)
			.takeIf { it is Property<*> } as Property<*>?
}
