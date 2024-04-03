package me.celestialfault.celestialconfig.variables.abstracts;

import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility superclass for loading and saving number values
 *
 * @param <T> The number type to store
 *
 * @see me.celestialfault.celestialconfig.variables.FloatVariable
 * @see me.celestialfault.celestialconfig.variables.DoubleVariable
 * @see me.celestialfault.celestialconfig.variables.IntegerVariable
 */
public abstract class NumberVariable<T extends Number & Comparable<T>> extends PrimitiveVariable<T> {
	/**
	 * Minimum inclusive value; if {@code null}, this value has no minimum.
	 */
	protected final @Nullable T min;
	/**
	 * Maximum inclusive value; if {@code null}, this value has no maximum.
	 */
	protected final @Nullable T max;

	/**
	 * Create a new number variable
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 * @param min          The minimum (inclusive) value; this may be {@code null} if this value has no minimum
	 * @param max          The maximum (inclusive) value; this may be {@code null} if this value has no maximum
	 */
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
