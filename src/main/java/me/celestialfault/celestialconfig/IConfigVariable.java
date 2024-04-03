package me.celestialfault.celestialconfig;

import com.google.gson.JsonElement;
import me.celestialfault.celestialconfig.variables.VariableMap;
import me.celestialfault.celestialconfig.variables.abstracts.ConfigVariable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Configuration variable interface; this is what stores your loaded configuration values.
 *
 * @param <T> The type of the stored variable
 *
 * @see ConfigVariable
 * @see VariableMap
 */
public interface IConfigVariable<T> {
	/**
	 * Returns the value stored in this variable
	 */
	T get();

	/**
	 * Get the key this variable is stored in configuration files
	 */
	String getKey();

	/**
	 * Validate the current value
	 *
	 * @implNote The default implementation of this method always returns {@code true} if the provided value is not {@code null}
	 *
	 * @param value The value to validate
	 * @return {@code true} if the provided {@code value} is allowed to be stored in this variable
	 */
	default boolean validate(@Nullable T value) {
		return value != null;
	}

	/**
	 * Loads this variable's value from the provided {@link JsonElement}
	 *
	 * @param element The {@link JsonElement} loaded from the configuration file saved on disk
	 */
	void load(JsonElement element);

	/**
	 * Encode a {@link JsonElement} for use in saving to disk
	 *
	 * @return An {@link Optional} containing a {@link JsonElement}, or an empty {@link Optional} if this variable
	 *         shouldn't be saved to disk.
	 */
	Optional<JsonElement> save();
}
