package me.celestialfault.celestialconfig.json;

import com.google.gson.JsonElement;

/**
 * <p>Functional interface deserializing a {@link JsonElement} to a value able to be stored in a
 * {@link me.celestialfault.celestialconfig.variables.abstracts.ConfigVariable ConfigVariable}</p>
 *
 * @param <T> The type that the provided {@link T value} should be loaded from the deserialized {@link JsonElement}
 */
@FunctionalInterface
public interface Deserializer<T> {
	T deserialize(JsonElement element);
}
