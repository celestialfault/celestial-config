package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.NumberVariable;
import org.jetbrains.annotations.Nullable;

public class IntegerVariable extends NumberVariable<Integer> {
	public IntegerVariable(String key, @Nullable Integer defaultValue) {
		super(key, defaultValue, null, null);
	}

	public IntegerVariable(String key, @Nullable Integer defaultValue, @Nullable Integer min, @Nullable Integer max) {
		super(key, defaultValue, min, max);
	}

	@Override
	protected Integer fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsInt();
	}
}
