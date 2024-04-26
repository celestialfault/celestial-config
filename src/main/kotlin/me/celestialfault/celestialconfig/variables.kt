package me.celestialfault.celestialconfig

import me.celestialfault.celestialconfig.properties.ObjectProperty
import org.jetbrains.annotations.ApiStatus.Internal
import org.jetbrains.annotations.Unmodifiable
import java.lang.reflect.Modifier
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * @suppress Not intended to be part of the public API
 */
@Internal
abstract class VariableLookup protected constructor() {
	val variables: @Unmodifiable Map<String, Property<*>> by lazy {
		val map = mutableMapOf<String, Property<*>>()

		map.putAll(actualVariables)
		map.putAll(delegated)
		map.putAll(objects)

		Collections.unmodifiableMap(map)
	}

	private val actualVariables by lazy {
		this@VariableLookup::class.memberProperties
			.asSequence()
			.filter { it.returnType.isSubtypeOf(Property::class.starProjectedType) }
			.onEach { check(it.javaField != null) { "Property fields cannot make use of get() syntax" } }
			.onEach { check(Modifier.isFinal(it.javaField!!.modifiers)) { "Property field must be final" } }
			.map { it.getter.call(this@VariableLookup) as Property<*> }
			.onEach { require(it.key.isNotEmpty()) { "Property key cannot be an empty string" } }
			.associateBy { it.key }
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
