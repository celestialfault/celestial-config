package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import me.celestialfault.celestialconfig.IConfigVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * <p>A {@link Map} variable containing values of one type, mapped to string key names.</p>
 *
 * <p>The underlying {@link HashMap} may be accessed with {@link #get()}, but this class may also be used directly
 * as a {@link Map}.</p>
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * public final class MyConfig extends AbstractConfig {
 *     public final Map2ObjectVariable<Integer> ints = new Map2ObjectVariable<>("ints", JsonPrimitive::new, element -> element.getAsInt());
 * }
 *
 * // elsewhere:
 *
 * MyConfig config = new MyConfig();
 * config.load();
 * config.ints.put("key", random());
 * config.ints.get("key"); // -> 6
 * config.save();
 * }</pre>
 */
public class Map2ObjectVariable<T> implements IConfigVariable<Map<String, T>>, Map<String, T> {
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
			element.getAsJsonObject().asMap().forEach((k, v) -> {
				if(v.isJsonNull()) {
					map.put(k, null);
				} else {
					map.put(k, deserializer.deserialize(v));
				}
			});
		}
	}

	@Override
	public Optional<JsonElement> save() {
		JsonObject object = new JsonObject();
		map.forEach((k, v) -> {
			if(v == null) {
				object.add(k, JsonNull.INSTANCE);
			} else {
				object.add(k, serializer.serialize(v));
			}
		});
		return Optional.of(object);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean containsValue(Object o) {
		return map.containsValue(o);
	}

	@Override
	public T get(Object o) {
		return map.get(o);
	}

	@Nullable
	@Override
	public T put(String s, T t) {
		return map.put(s, t);
	}

	@Override
	public T remove(Object o) {
		return map.remove(o);
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends T> map) {
		this.map.putAll(map);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@NotNull
	@Override
	public Collection<T> values() {
		return map.values();
	}

	@NotNull
	@Override
	public Set<Entry<String, T>> entrySet() {
		return map.entrySet();
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
