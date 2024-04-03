package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.ConfigVariable;

import java.util.Arrays;
import java.util.Optional;

/**
 * <p>Variable storing an enumeration value</p>
 *
 * <p>This variable accepts string enum value names or integer ordinal values when loading from JSON,
 * but only writes to JSON with the enum value name.</p>
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * class MyEnum {
 *     A, B, C
 * }
 *
 * public class MyConfig extends AbstractConfig {
 *     EnumVariable<MyEnum> enumVar = new EnumVariable<>("enum", MyEnum.class, MyEnum.A);
 * }
 * }</pre>
 *
 * @apiNote This variable is not designed to support a large amount of {@link #load(JsonElement)} calls on an enum with
 *          a substantial amount of values; if you have such a use case that requires this variable to be performant
 *          in such a use case, you should implement this variable yourself.
 *
 * @param <T> The {@link Enum} class type for this variable
 */
public class EnumVariable<T extends Enum<?>> extends ConfigVariable<T> {
	private final T[] values;

	/**
	 * Create a new enum variable with the provided enum class
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param enumClass    The target {@link Enum} class
	 * @param defaultValue The default value returned if none is set
	 */
	public EnumVariable(String key, Class<T> enumClass, T defaultValue) {
		super(key, defaultValue);
		this.values = enumClass.getEnumConstants();
	}

	@Override
	public void load(JsonElement element) {
		if(element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			int ordinal = element.getAsInt();
			if(0 >= ordinal && ordinal < values.length) {
				set(values[ordinal]);
			}
		} else if(element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
			String name = element.getAsString();
			Arrays.stream(values).filter(x -> x.name().equalsIgnoreCase(name)).findFirst().ifPresent(this::set);
		}
	}

	@Override
	public Optional<JsonElement> save() {
		if(value == null) return Optional.empty();
		return Optional.of(new JsonPrimitive(value.name()));
	}
}
