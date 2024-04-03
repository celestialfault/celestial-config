package me.celestialfault.celestialconfig.variables.abstracts;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Utility superclass for loading and saving {@link JsonPrimitive} values
 *
 * @param <T> The type of the stored variable
 */
public abstract class PrimitiveVariable<T> extends ConfigVariable<T> {
	/**
	 * Create a new {@link JsonPrimitive} variable instance
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 */
	protected PrimitiveVariable(String key, @Nullable T defaultValue) {
		super(key, defaultValue);
	}

	/**
	 * Check if the provided {@link JsonPrimitive} is valid for this class type, typically with one of the
	 * {@code #isType()} methods provided by {@link JsonPrimitive}.
	 *
	 * @param primitive The {@link JsonPrimitive} to validate
	 */
	protected abstract boolean isPrimitiveValid(JsonPrimitive primitive);

	/**
	 * Convert the provided {@link JsonPrimitive} to an object able to be stored in this variable
	 *
	 * @param primitive The {@link JsonPrimitive} to read from
	 * @return The value read from the provided primitive
	 */
	protected abstract T fromPrimitive(JsonPrimitive primitive);

	/**
	 * Convert the stored value in this variable to a {@link JsonPrimitive}
	 *
	 * @param value The current value stored in this {@link PrimitiveVariable}
	 * @return An encoded {@link JsonPrimitive} for the provided value
	 */
	protected abstract JsonPrimitive toPrimitive(@NotNull T value);

	@Override
	public final void load(JsonElement element) {
		if(element.isJsonNull() && allowNulls) {
			set(null);
		} else if(element.isJsonPrimitive() && isPrimitiveValid(element.getAsJsonPrimitive())) {
			set(fromPrimitive(element.getAsJsonPrimitive()));
		}
	}

	@Override
	public final Optional<JsonElement> save() {
		@Nullable T value = get();
		if(value == null && defaultValue == null) {
			return Optional.ofNullable(allowNulls ? JsonNull.INSTANCE : null);
		}
		return Optional.of(toPrimitive(value != null ? value : defaultValue));
	}
}
