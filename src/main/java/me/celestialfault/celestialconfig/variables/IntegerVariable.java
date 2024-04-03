package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.NumberVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Variable storing an integer value
 */
public class IntegerVariable extends NumberVariable<Integer> {
	/**
	 * Create a new integer value with no min/max values
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 */
	public IntegerVariable(String key, @Nullable Integer defaultValue) {
		super(key, defaultValue, null, null);
	}

	/**
	 * Create a new integer variable with min/max allowed values; these maximum values are inclusive.
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 * @param min          The minimum inclusive integer value; can be null to have no minimum
	 * @param max          The maximum inclusive integer value; can be null to have no maximum
	 */
	public IntegerVariable(String key, @Nullable Integer defaultValue, @Nullable Integer min, @Nullable Integer max) {
		super(key, defaultValue, min, max);
	}

	@Override
	protected Integer fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsInt();
	}
}
