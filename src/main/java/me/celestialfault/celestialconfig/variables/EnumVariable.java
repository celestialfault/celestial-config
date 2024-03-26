package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.variables.abstracts.ConfigVariable;

import java.util.Arrays;
import java.util.Optional;

/**
 * Variable storing an enumeration value; this writes to JSON with the name of the enum value,
 * but also accepts reading in an ordinal value from disk.
 */
public class EnumVariable<T extends Enum<?>> extends ConfigVariable<T> {
	private final T[] values;

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
