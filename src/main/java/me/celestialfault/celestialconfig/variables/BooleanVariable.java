package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.PrimitiveVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Variable storing a boolean value
 */
public class BooleanVariable extends PrimitiveVariable<Boolean> {
	/**
	 * Create a new {@link Boolean} variable
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 */
	public BooleanVariable(String key, @Nullable Boolean defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected boolean isPrimitiveValid(JsonPrimitive primitive) {
		return primitive.isBoolean();
	}

	@Override
	protected Boolean fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsBoolean();
	}

	@Override
	protected JsonPrimitive toPrimitive(@NotNull Boolean value) {
		return new JsonPrimitive(value);
	}
}
