package me.celestialfault.celestialconfig.variables.abstracts;

import com.google.gson.JsonElement;
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
	 *
	 * @apiNote This will only be called if the return value of {@link #get()} is not {@code null}, and as such
	 *          you can safely make use of methods like {@link Objects#requireNonNull(Object)} without worrying
	 *          about potential runtime errors.
	 */
	protected abstract JsonPrimitive toPrimitive(@NotNull T value);

	@Override
	public final void load(JsonElement element) {
		if(element.isJsonPrimitive() && isPrimitiveValid(element.getAsJsonPrimitive())) {
			set(fromPrimitive(element.getAsJsonPrimitive()));
		}
	}

	@Override
	public final Optional<JsonElement> save() {
		@Nullable T value = get();
		if(value == null && defaultValue == null) {
			return Optional.empty();
		}
		return Optional.of(toPrimitive(value != null ? value : defaultValue));
	}
}
