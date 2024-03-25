package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.NumberVariable;
import org.jetbrains.annotations.Nullable;

public class DoubleVariable extends NumberVariable<Double> {
	public DoubleVariable(String key, @Nullable Double defaultValue) {
		super(key, defaultValue, null, null);
	}

	public DoubleVariable(String key, @Nullable Double defaultValue, @Nullable Double min, @Nullable Double max) {
		super(key, defaultValue, min, max);
	}

	@Override
	protected Double fromPrimitive(JsonPrimitive primitive) {
		return primitive.getAsDouble();
	}
}
