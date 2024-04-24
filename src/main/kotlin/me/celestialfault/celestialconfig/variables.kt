package me.celestialfault.celestialconfig

import me.celestialfault.celestialconfig.properties.ObjectProperty
import org.jetbrains.annotations.ApiStatus.Internal
import org.jetbrains.annotations.Unmodifiable
import java.lang.reflect.Modifier
import java.util.*
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaField

/**
 * @suppress Not intended to be part of the public API
 */
@Internal
abstract class VariableLookup protected constructor() {
	val variables: @Unmodifiable Map<String, Property<*>> by lazy {
		mutableMapOf<String, Property<*>>().apply {
			this@VariableLookup::class.memberProperties
				.asSequence()
				.filter { it.visibility == KVisibility.PUBLIC }
				.filter { it.returnType.isSubtypeOf(Property::class.starProjectedType) }
				.onEach { require(it.javaField != null) { "Property fields cannot make use of get() syntax" } }
				.filter { it.javaField!!.let { field -> Modifier.isFinal(field.modifiers) } }
				.map { it.getter.call(this@VariableLookup) as Property<*> }
				.onEach { require(it.key !in this) { "Duplicate key ${it.key}" } }
				.onEach { require(it.key.isNotEmpty()) { "Property key cannot be an empty string" } }
				.forEach { this[it.key] = it }

			// Explicitly look for any kotlin object types
			this@VariableLookup::class.nestedClasses
				.asSequence()
				.filter { it.visibility == KVisibility.PUBLIC }
				.filter { it.objectInstance != null }
				.filter { it.isSubclassOf(ObjectProperty::class) }
				.map { it.objectInstance as ObjectProperty<*> }
				.onEach { require(it.key !in this) { "Duplicate key ${it.key}" } }
				.onEach { require(it.key.isNotEmpty()) { "Object key cannot be an empty string" } }
				.forEach { this[it.key] = it }
		}.let { Collections.unmodifiableMap(it) }
	}
}
