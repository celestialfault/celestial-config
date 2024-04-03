package me.celestialfault.celestialconfig.variables.abstracts;

import me.celestialfault.celestialconfig.IConfigVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Abstract configuration variable class for variable types that require little (if any at all) special handling
 * to load and save.
 *
 * @param <T> The type of the stored variable
 */
public abstract class ConfigVariable<T> implements IConfigVariable<T> {
	/**
	 * The key that this variable is stored in when saving to/loading from disk
	 */
	protected final String key;
	/**
	 * The current stored variable; you should avoid setting this directly outside the class constructor,
	 * and instead use {@link #set}.
	 */
	protected @Nullable T value = null;

	/**
	 * Default value passed when creating this variable.
	 */
	public final @Nullable T defaultValue;
	/**
	 * If {@code true}, this variable will accept null values; by default, this is only {@code true}
	 * if {@link #defaultValue} is {@code null}.
	 */
	public boolean allowNulls;

	/**
	 * Create a new {@link ConfigVariable} instance
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param defaultValue The default value returned if none is set
	 */
	protected ConfigVariable(String key, @Nullable T defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
		this.allowNulls = defaultValue == null;
	}

	@Override
	public String getKey() {
		return key;
	}

	/**
	 * Get the current stored value for this variable
	 *
	 * @implNote This <i>may</i> return {@code null}, but only if either {@link #allowNulls} is {@code true},
	 *           or if this variable was created with a {@code null} default value.
	 */
	@Override
	@UnknownNullability
	public T get() {
		if(this.value == null && !allowNulls) {
			return this.defaultValue;
		}
		return this.value;
	}

	/**
	 * Set the value stored in this config variable
	 *
	 * @implNote This defaults to only setting the variable if {@link #validate} returns {@code true}
	 *
	 * @param value The value to store in this {@link ConfigVariable}
	 */
	public void set(@Nullable T value) {
		if(validate(value)) {
			this.value = value;
		}
	}

	@Override
	public boolean validate(@Nullable T value) {
		return value != null || allowNulls;
	}

	/**
	 * Convenience builder-like method for setting if this variable should allow nulls if your default value
	 * isn't null.
	 *
	 * @param allowNulls The value to set {@link #allowNulls} to
	 * @return The current {@link ConfigVariable} instance
	 *
	 * @see #allowNulls
	 */
	@Contract("_ -> this")
	public ConfigVariable<T> allowNulls(boolean allowNulls) {
		this.allowNulls = allowNulls;
		return this;
	}
}
