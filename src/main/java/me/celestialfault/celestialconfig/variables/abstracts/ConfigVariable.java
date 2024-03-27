package me.celestialfault.celestialconfig.variables.abstracts;

import me.celestialfault.celestialconfig.IConfigVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Abstract configuration variable class for variable types that require little (if any at all) special handling
 * to load and save.
 */
public abstract class ConfigVariable<T> implements IConfigVariable<T> {
	protected final String key;
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
	 */
	@Override
	@UnknownNullability("see #allowNulls")
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
	 */
	@Contract("_ -> this")
	public ConfigVariable<T> allowNulls(boolean allowNulls) {
		this.allowNulls = allowNulls;
		return this;
	}
}
