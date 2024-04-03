package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import me.celestialfault.celestialconfig.json.Deserializer;
import me.celestialfault.celestialconfig.IConfigVariable;
import me.celestialfault.celestialconfig.json.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * <p>Variable storing a {@link Map} with values of one type, mapped to {@link String} key names</p>
 *
 * <p>The underlying {@link Map} may be accessed with {@link #get()}, but this class may also be used directly
 * as a {@link Map}.</p>
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * public final class MyConfig extends AbstractConfig {
 *     public final MapVariable<Integer> ints = new MapVariable<>("ints", JsonPrimitive::new, JsonElement::getAsInt);
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
 *
 * @param <T> The type of this map's values
 */
public class MapVariable<T> implements IConfigVariable<Map<String, T>>, Map<String, T> {
	private final String key;
	private final Map<String, T> map = new HashMap<>();
	private final Serializer<T> serializer;
	private final Deserializer<T> deserializer;

	/**
	 * Create a new map variable using the provided default values map
	 *
	 * @param key           The key this variable is stored as in configuration files
	 * @param defaultValues The default values to load into the created variable
	 * @param serializer    The serialization method for contained values used when saving this variable to disk
	 * @param deserializer  The deserialization method for contained values used when loading this variable from disk
	 */
	public MapVariable(String key, Map<String, T> defaultValues, Serializer<T> serializer, Deserializer<T> deserializer) {
		this(key, serializer, deserializer);
		this.map.putAll(defaultValues);
	}

	/**
	 * Create a new empty map variable
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param serializer   The serialization method for contained values used when saving this variable to disk
	 * @param deserializer The deserialization method for contained values used when loading this variable from disk
	 */
	public MapVariable(String key, Serializer<T> serializer, Deserializer<T> deserializer) {
		this.key = key;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public String getKey() {
		return key;
	}

	/**
	 * Get the underlying {@link Map} that this variable mirrors; in most cases you shouldn't need to
	 * make use of this method, as this {@link MapVariable<T>} will reflect all relevant map methods
	 * onto the underlying map.
	 */
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
}
