package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.NumberVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Variable storing a double value
 */
public class DoubleVariable extends NumberVariable<Double> {
	/**
	 * Create a new {@link Double} variable with no minimum or maximum value
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 */
	public DoubleVariable(String key, @Nullable Double defaultValue) {
		super(key, defaultValue, null, null);
	}

	/**
	 * Create a new {@link Double} variable with a minimum and/or maximum
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 * @param min          The minimum (inclusive) value; this may be {@code null} if this value has no minimum
	 * @param max          The maximum (inclusive) value; this may be {@code null} if this value has no maximum
	 */
	public DoubleVariable(String key, @Nullable Double defaultValue, @Nullable Double min, @Nullable Double max) {
		super(key, defaultValue, min, max);
	}

	@Override
	protected Double fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsDouble();
	}
}
