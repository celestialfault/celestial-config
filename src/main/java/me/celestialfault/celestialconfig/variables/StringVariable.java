package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.PrimitiveVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Variable storing a string value
 */
public class StringVariable extends PrimitiveVariable<String> {
	/**
	 * Create a new {@link String} variable
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 */
	public StringVariable(String key, @Nullable String defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected boolean isPrimitiveValid(JsonPrimitive primitive) {
		return primitive.isString();
	}

	@Override
	protected String fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsString();
	}

	@Override
	protected JsonPrimitive toPrimitive(@NotNull String value) {
		return new JsonPrimitive(value);
	}
}
