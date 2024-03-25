package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.PrimitiveVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringVariable extends PrimitiveVariable<String> {
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
