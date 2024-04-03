package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import me.celestialfault.celestialconfig.json.Deserializer;
import me.celestialfault.celestialconfig.IConfigVariable;
import me.celestialfault.celestialconfig.json.Serializer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * <p>Variable storing a {@link List} with elements of one type</p>
 *
 * <p>The underlying {@link List} may be accessed with {@link #get()}, but this class may also be used directly
 * as a {@link List}.</p>
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * public final class MyConfig extends AbstractConfig {
 *     public final ArrayVariable<Integer> ints = new ArrayVariable<>("ints", JsonPrimitive::new, element -> element.getAsInt());
 * }
 *
 * // elsewhere:
 *
 * MyConfig config = new MyConfig();
 * config.load();
 * config.ints.add(random());
 * config.ints.get(0); // -> 6
 * config.save();
 * }</pre>
 *
 * @param <T> The type of this list's contents
 */
public class ArrayVariable<T> implements IConfigVariable<List<T>>, List<T> {
	private final String key;
	private final List<T> list = new ArrayList<>();
	private final Serializer<T> serializer;
	private final Deserializer<T> deserializer;

	/**
	 * Create a new array variable with a default list of values
	 *
	 * @param key           The key this variable is stored as in configuration files
	 * @param defaultValues The default values to load into the created variable
	 * @param serializer    The serialization method for contained values used when saving this variable to disk
	 * @param deserializer  The deserialization method for contained values used when loading this variable from disk
	 */
	public ArrayVariable(String key, List<T> defaultValues, Serializer<T> serializer, Deserializer<T> deserializer) {
		this(key, serializer, deserializer);
		this.list.addAll(defaultValues);
	}

	/**
	 * Create a new empty array variable
	 *
	 * @param key          The key this variable is stored as in configuration files
	 * @param serializer   The serialization method for contained values used when saving this variable to disk
	 * @param deserializer The deserialization method for contained values used when loading this variable from disk
	 */
	public ArrayVariable(String key, Serializer<T> serializer, Deserializer<T> deserializer) {
		this.key = key;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public String getKey() {
		return key;
	}

	/**
	 * Get the underlying {@link List} that this variable mirrors; in most cases you shouldn't need to
	 * make use of this method, as this {@link ArrayVariable} will reflect all relevant list methods
	 * onto the underlying list.
	 */
	@Override
	public @NotNull List<T> get() {
		return this.list;
	}

	@Override
	public void load(JsonElement element) {
		if(element.isJsonArray()) {
			list.clear();
			element.getAsJsonArray().forEach(v -> {
				if(v.isJsonNull()) {
					list.add(null);
				} else {
					list.add(deserializer.deserialize(v));
				}
			});
		}
	}

	@Override
	public Optional<JsonElement> save() {
		JsonArray array = new JsonArray();
		list.forEach(v -> {
			if(v == null) {
				array.add(JsonNull.INSTANCE);
			} else {
				array.add(serializer.serialize(v));
			}
		});
		return Optional.of(array);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	@NotNull
	@Override
	@SuppressWarnings("NullableProblems")
	public Object[] toArray() {
		return list.toArray();
	}

	@NotNull
	@Override
	@SuppressWarnings("NullableProblems")
	public <T1> T1[] toArray(@NotNull T1[] t1s) {
		return list.toArray(t1s);
	}

	@Override
	public boolean add(T t) {
		return list.add(t);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	// We're simply acting as a proxy for the underlying list
	@SuppressWarnings("SlowListContainsAll")
	@Override
	public boolean containsAll(@NotNull Collection<?> collection) {
		return list.containsAll(collection);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends T> collection) {
		return list.addAll(collection);
	}

	@Override
	public boolean addAll(int i, @NotNull Collection<? extends T> collection) {
		return list.addAll(i, collection);
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> collection) {
		return list.removeAll(collection);
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> collection) {
		return list.retainAll(collection);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public T get(int i) {
		return list.get(i);
	}

	@Override
	public T set(int i, T t) {
		return list.set(i, t);
	}

	@Override
	public void add(int i, T t) {
		list.add(i, t);
	}

	@Override
	public T remove(int i) {
		return list.remove(i);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator(int i) {
		return list.listIterator(i);
	}

	@NotNull
	@Override
	public List<T> subList(int i, int i1) {
		return list.subList(i, i1);
	}
}
