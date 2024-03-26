package me.celestialfault.celestialconfig.variables.abstracts;

import me.celestialfault.celestialconfig.IConfigVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract configuration variable class for variable types that require little (if any at all) special handling
 * to load and save.
 */
public abstract class ConfigVariable<T> implements IConfigVariable<T> {
	protected final String key;
	protected final @Nullable T defaultValue;
	protected @Nullable T value;

	public ConfigVariable(String key, @Nullable T defaultValue) {
		this.key = key;
		this.value = this.defaultValue = defaultValue;
	}

	@Override
	public String getKey() {
		return key;
	}

	/**
	 * Get the current stored value for this variable
	 */
	@Override
	public @Nullable T get() {
		return this.value;
	}

	/**
	 * Set the value stored in this config variable
	 *
	 * @implNote This defaults to only setting the variable if {@link #validate} returns {@code true}
	 */
	public void set(@Nullable T value) {
		if(validate(value)) {
			this.value = value;
		}
	}
}
