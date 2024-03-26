package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.celestialfault.celestialconfig.IConfigVariable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Map of key names to stored configuration values
 */
public class Map2ObjectVariable<T> implements IConfigVariable<Map<String, T>> {
	private final String key;
	private final Map<String, T> map = new HashMap<>();
	private final Serializer<T> serializer;
	private final Deserializer<T> deserializer;

	public Map2ObjectVariable(String key, Map<String, T> defaultValues, Serializer<T> serializer, Deserializer<T> deserializer) {
		this(key, serializer, deserializer);
		this.map.putAll(defaultValues);
	}

	public Map2ObjectVariable(String key, Serializer<T> serializer, Deserializer<T> deserializer) {
		this.key = key;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public @NotNull Map<String, T> get() {
		return map;
	}

	@Override
	public void load(JsonElement element) {
		if(element.isJsonObject()) {
			map.clear();
			element.getAsJsonObject().asMap().forEach((k, v) -> map.put(k, deserializer.deserialize(v)));
		}
	}

	@Override
	public Optional<JsonElement> save() {
		JsonObject object = new JsonObject();
		map.forEach((k, v) -> object.add(k, serializer.serialize(v)));
		return Optional.of(object);
	}

	@FunctionalInterface
	public interface Serializer<T> {
		JsonElement serialize(T value);
	}

	@FunctionalInterface
	public interface Deserializer<T> {
		T deserialize(JsonElement element);
	}
}
