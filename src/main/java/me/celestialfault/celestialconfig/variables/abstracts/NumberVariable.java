package me.celestialfault.celestialconfig.variables.abstracts;

import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NumberVariable<T extends Number & Comparable<T>> extends PrimitiveVariable<T> {
	protected final @Nullable T min;
	protected final @Nullable T max;

	protected NumberVariable(String key, @Nullable T defaultValue, @Nullable T min, @Nullable T max) {
		super(key, defaultValue);
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean validate(@Nullable T value) {
		if(value == null) {
			return allowNulls;
		}
		return (min == null || min.compareTo(value) <= 0)
			&& (max == null || max.compareTo(value) >= 0);
	}

	@Override
	protected boolean isPrimitiveValid(JsonPrimitive primitive) {
		return primitive.isNumber();
	}

	@Override
	protected JsonPrimitive toPrimitive(@NotNull T value) {
		return new JsonPrimitive(value);
	}
}
