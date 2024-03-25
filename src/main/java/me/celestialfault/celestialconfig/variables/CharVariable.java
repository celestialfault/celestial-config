package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.PrimitiveVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CharVariable extends PrimitiveVariable<Character> {
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
