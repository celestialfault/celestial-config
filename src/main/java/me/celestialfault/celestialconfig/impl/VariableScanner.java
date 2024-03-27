package me.celestialfault.celestialconfig.impl;

import me.celestialfault.celestialconfig.IConfigVariable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Utility class which scans for {@link IConfigVariable}s on a class</p>
 *
 * <p>Such variables will only be discovered if the field is <b>{@code public}</b>, <b>{@code non-static}</b>,
 * and <b>{@code final}</b>.</p>
 */
public abstract class VariableScanner {
	private @Unmodifiable Map<String, IConfigVariable<?>> variables = null;

	protected @Unmodifiable Map<String, ? extends IConfigVariable<?>> getVariables() {
		if(variables == null) {
			Map<String, IConfigVariable<?>> discovered = new LinkedHashMap<>();
			Arrays.stream(this.getClass().getFields())
				.filter(field -> IConfigVariable.class.isAssignableFrom(field.getType()))
				.filter(field -> Modifier.isPublic(field.getModifiers()))
				.filter(field -> !Modifier.isStatic(field.getModifiers()))
				.filter(field -> Modifier.isFinal(field.getModifiers()))
				.map(field -> {
					try {
						return (IConfigVariable<?>) field.get(this);
					} catch(IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
				.forEach(variable -> {
					if(discovered.containsKey(variable.getKey())) {
						throw new IllegalStateException("Key " + variable.getKey() + " is present twice!");
					}
					discovered.put(variable.getKey(), variable);
				});
			this.variables = Collections.unmodifiableMap(discovered);
		}
		return variables;
	}
}
