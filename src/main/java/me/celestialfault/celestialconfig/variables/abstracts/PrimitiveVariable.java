package me.celestialfault.celestialconfig.variables.abstracts;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Utility class for loading and saving {@link JsonPrimitive} values
 */
public abstract class PrimitiveVariable<T> extends ConfigVariable<T> {
	protected PrimitiveVariable(String key, @Nullable T defaultValue) {
		super(key, defaultValue);
	}

	/**
	 * Check if the provided {@link JsonPrimitive} is valid for this class type, typically with one of the
	 * {@code #isType()} methods provided by {@link JsonPrimitive}.
	 */
	protected abstract boolean isPrimitiveValid(JsonPrimitive primitive);

	/**
	 * Convert the provided {@link JsonPrimitive} to an object able to be stored in this variable
	 */
	protected abstract T fromPrimitive(JsonPrimitive primitive);

	/**
	 * Convert the stored value in this variable to a {@link JsonPrimitive}
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
