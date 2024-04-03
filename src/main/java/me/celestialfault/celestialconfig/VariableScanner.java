package me.celestialfault.celestialconfig;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class to find all {@link IConfigVariable} fields on the current class instance
 *
 * @apiNote This class is not intended to be used by API consumers, and has no compatibility guarantee.
 */
@ApiStatus.Internal
public abstract class VariableScanner {
	/**
	 * Construct a new {@link VariableScanner} instance
	 */
	protected VariableScanner() {}

	private @Unmodifiable Map<String, IConfigVariable<?>> variables = null;

	/**
	 * Get all {@link IConfigVariable} fields on this class, provided that they are all <b>{@code public}</b>,
	 * <b>{@code final}</b>, and <b>{@code non-static}</b>.
	 *
	 * @return A map of {@link IConfigVariable#getKey()} to {@link IConfigVariable}
	 *
	 * @throws IllegalStateException If a variable key is {@code null}, or if more than one variable uses the same key
	 */
	protected @Unmodifiable Map<String, ? extends IConfigVariable<?>> getVariables() {
		if(variables == null) {
			Map<String, IConfigVariable<?>> discovered = new LinkedHashMap<>();
			Arrays.stream(this.getClass().getFields())
				.filter(field -> IConfigVariable.class.isAssignableFrom(field.getType()))
				.filter(field -> {
					int modifiers = field.getModifiers();
					return Modifier.isPublic(modifiers)
						&& !Modifier.isStatic(modifiers)
						&& Modifier.isFinal(modifiers);
				})
				.map(field -> {
					try {
						return (IConfigVariable<?>) field.get(this);
					} catch(IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
				.forEach(variable -> {
					if(variable.getKey() == null) {
						throw new IllegalStateException("Variables are not allowed to have null keys");
					}
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
