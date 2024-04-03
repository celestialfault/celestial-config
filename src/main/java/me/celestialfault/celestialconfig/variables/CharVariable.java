package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.PrimitiveVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Variable storing a character value
 *
 * @implNote This internally loads and saves as a {@link String}, but will only read the first character
 *           of the loaded string.
 */
public class CharVariable extends PrimitiveVariable<Character> {
	/**
	 * Create a new {@link Character} variable
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 */
	public CharVariable(String key, @Nullable Character defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected boolean isPrimitiveValid(JsonPrimitive primitive) {
		return primitive.isString();
	}

	@Override
	protected Character fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsString().charAt(0);
	}

	@Override
	protected JsonPrimitive toPrimitive(@NotNull Character value) {
		return new JsonPrimitive(value);
	}
}
