package me.celestialfault.celestialconfig;

import com.google.gson.JsonElement;
import me.celestialfault.celestialconfig.variables.VariableMap;
import me.celestialfault.celestialconfig.variables.abstracts.ConfigVariable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Configuration value interface; this is what stores your loaded configuration values.
 *
 * @see ConfigVariable
 * @see VariableMap
 */
public interface IConfigVariable<T> {
	String getKey();
	T get();

	/**
	 * Validate the current value
	 *
	 * @implNote The default implementation of this method always returns {@code true}
	 */
	default boolean validate(@Nullable T value) {
		return true;
	}

	void load(JsonElement element);
	Optional<JsonElement> save();
}
