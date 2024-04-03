package me.celestialfault.celestialconfig.variables;

import me.celestialfault.celestialconfig.json.Deserializer;
import me.celestialfault.celestialconfig.json.Serializer;

import java.util.Map;

/**
 * @deprecated This class has been renamed to {@link MapVariable}
 */
@Deprecated
public class Map2ObjectVariable<T> extends MapVariable<T> {
	public Map2ObjectVariable(String key, Map<String, T> defaultValues, Serializer<T> serializer, Deserializer<T> deserializer) {
		super(key, defaultValues, serializer, deserializer);
	}

	public Map2ObjectVariable(String key, Serializer<T> serializer, Deserializer<T> deserializer) {
		super(key, serializer, deserializer);
	}
}
