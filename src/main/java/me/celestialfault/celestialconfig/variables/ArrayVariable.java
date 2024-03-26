package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.celestialfault.celestialconfig.IConfigVariable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A variable containing a list of elements of one type
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * class MyConfig extends AbstractConfig {
 *     public final ArrayVariable<Integer> ints = new ArrayVariable("ints", JsonPrimitive::new, element -> element.getAsInt());
 * }
 *
 * // elsewhere:
 *
 * MyConfig config = new MyConfig();
 * config.load();
 * List<Integer> ints = config.ints.get();
 * ints.add(random());
 * ints.get(0); // -> 6
 * config.save();
 * }</pre>
 */
public class ArrayVariable<T> implements IConfigVariable<List<T>> {
	private final String key;
	private final List<T> list = new ArrayList<>();
	private final Serializer<T> serializer;
	private final Deserializer<T> deserializer;

	public ArrayVariable(String key, List<T> defaultValues, Serializer<T> serializer, Deserializer<T> deserializer) {
		this(key, serializer, deserializer);
		this.list.addAll(defaultValues);
	}

	public ArrayVariable(String key, Serializer<T> serializer, Deserializer<T> deserializer) {
		this.key = key;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public @NotNull List<T> get() {
		return this.list;
	}

	@Override
	public void load(JsonElement element) {
		if(element.isJsonArray()) {
			list.clear();
			element.getAsJsonArray().forEach(e -> list.add(deserializer.deserialize(e)));
		}
	}

	@Override
	public Optional<JsonElement> save() {
		JsonArray array = new JsonArray();
		list.forEach(v -> array.add(serializer.serialize(v)));
		return Optional.of(array);
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
