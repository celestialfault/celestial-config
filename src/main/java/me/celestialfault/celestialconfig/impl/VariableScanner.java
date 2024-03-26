package me.celestialfault.celestialconfig.impl;

import me.celestialfault.celestialconfig.IConfigVariable;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class which scans for {@link IConfigVariable}s on a class upon it being instantiated;
 * such variables will only be discovered if the field is <b>{@code public}</b>, <b>{@code non-static}</b>,
 * and <b>{@code final}</b>.
 */
public abstract class VariableScanner {
	private Map<String, IConfigVariable<?>> variables = null;

	protected Map<String, ? extends IConfigVariable<?>> getVariables() {
		if(variables == null) {
			variables = new LinkedHashMap<>();
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
					if(variables.containsKey(variable.getKey())) {
						throw new IllegalStateException("Key " + variable.getKey() + " is present twice!");
					}
					variables.put(variable.getKey(), variable);
				});
		}
		return variables;
	}
}
