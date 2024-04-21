package me.celestialfault.celestialconfig

import me.celestialfault.celestialconfig.properties.SubclassProperty
import org.jetbrains.annotations.ApiStatus.Internal
import java.lang.reflect.Modifier
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.javaField

/**
 * @suppress Not intended to be part of the public API
 */
@Internal
abstract class VariableLookup protected constructor() {
	private var internalVariables: Map<String, Property<*>>? = null
	val variables: Map<String, Property<*>>
		get() = internalVariables ?: findVariables()

	private fun findVariables(): Map<String, Property<*>> {
		val vars = this::class.memberProperties
			.asSequence()
			// Require that the field is public
			.filter { it.visibility == KVisibility.PUBLIC }
			// And that its final
			.filter { it.javaField?.let { field -> Modifier.isFinal(field.modifiers) } ?: (it !is KMutableProperty<*>) }
			// And that the field type is a Property implementation
			.filter { it.returnType.isSubtypeOf(Property::class.starProjectedType) }
			// Retrieve the property from the field, and store it in a map
			.map { it.getter.call(this) as Property<*> }
			.associateBy { it.key }
			.toMutableMap()

		// Explicitly look for any kotlin object types
		this::class.nestedClasses
			.filter { it.visibility == KVisibility.PUBLIC }
			.filter { it.objectInstance != null }
			.filter { it.isSubclassOf(SubclassProperty::class) }
			.map { it.objectInstance as SubclassProperty<*> }
			.forEach { vars[it.key] = it }

		this.internalVariables = vars
		return vars
	}
}
