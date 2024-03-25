package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.NumberVariable;
import org.jetbrains.annotations.Nullable;

public class FloatVariable extends NumberVariable<Float> {
	public FloatVariable(String key, @Nullable Float defaultValue) {
		super(key, defaultValue, null, null);
	}

	public FloatVariable(String key, @Nullable Float defaultValue, @Nullable Float min, @Nullable Float max) {
		super(key, defaultValue, min, max);
	}

	@Override
	protected Float fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsFloat();
	}
}
