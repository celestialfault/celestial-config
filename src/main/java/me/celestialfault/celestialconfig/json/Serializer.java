package me.celestialfault.celestialconfig.json;

import com.google.gson.JsonElement;

/**
 * <p>Functional interface serializing the current stored value to a {@link JsonElement}</p>
 *
 * <p>This is typically a method reference to {@link com.google.gson.JsonPrimitive#JsonPrimitive JsonPrimitive::new}</p>
 *
 * @param <T> The type that the provided {@link T value} which should be serialized
 */
@FunctionalInterface
public interface Serializer<T> {
	JsonElement serialize(T value);
}
