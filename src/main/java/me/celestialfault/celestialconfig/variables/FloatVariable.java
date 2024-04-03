package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.NumberVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Variable storing a float value
 */
public class FloatVariable extends NumberVariable<Float> {
	/**
	 * Create a new {@link Float} variable with no minimum or maximum value
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 */
	public FloatVariable(String key, @Nullable Float defaultValue) {
		super(key, defaultValue, null, null);
	}

	/**
	 * Create a new {@link Float} variable with a minimum and/or maximum
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 * @param min          The minimum (inclusive) value; this may be {@code null} if this value has no minimum
	 * @param max          The maximum (inclusive) value; this may be {@code null} if this value has no maximum
	 */
	public FloatVariable(String key, @Nullable Float defaultValue, @Nullable Float min, @Nullable Float max) {
		super(key, defaultValue, min, max);
	}

	@Override
	protected Float fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsFloat();
	}
}
