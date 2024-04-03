package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.celestialfault.celestialconfig.IConfigVariable;
import me.celestialfault.celestialconfig.VariableScanner;

import java.util.Map;
import java.util.Optional;

/**
 * <p>An {@link me.celestialfault.celestialconfig.AbstractConfig AbstractConfig}-like map variable</p>
 *
 * <p>This is designed as a more complex map variable, suitable for storing multiple different variable types,
 * unlike the one value type allowed by {@link MapVariable}.</p>
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * // in json, this would be saved as {"map": {"bool": true}}
 * public final class MyConfig extends AbstractConfig {
 *     // this field is how you access the variables contained in your map!
 *     public final MyMap map = new MyMap();
 *
 *     public static class MyMap extends VariableMap {
 *         private MyMap() {
 *             super("map");
 *         }
 *
 *         // access with MyConfig#map.bool
 *         public final BooleanVariable bool = new BooleanVariable("bool", true);
 *     }
 * }
 * }</pre>
 *
 * This could also be used as a type in an {@link ArrayVariable}:
 *
 * <pre>{@code
 * public final class MyMap extends VariableMap {
 *     public MyMap(JsonElement element) {
 *         super();
 *         this.load(element);
 *     }
 *
 *     public final StringVariable name = new StringVariable("name", null);
 *     public final DoubleVariable value = new DoubleVariable("value", 1);
 * }
 *
 * // in JSON, this would be saved as [{"name": "name", "value": 1.0}]
 * public final ArrayVariable<MyMap> arrayMap = new ArrayVariable<>("array_map",
 *          v -> v.save().orElse(null), MyMap::new);
 * }</pre>
 */
public abstract class VariableMap extends VariableScanner implements IConfigVariable<Map<String, IConfigVariable<?>>> {
	private final String key;

	/**
	 * Construct a {@link VariableMap} with an empty key; this is intended for nested use in types like {@link ArrayVariable}
	 */
	protected VariableMap() {
		this("");
	}

	/**
	 * Construct a {@link VariableMap} class to be stored in a configuration file in the provided {@code key}
	 *
	 * @param key The key this map is stored as in configuration files
	 */
	protected VariableMap(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, IConfigVariable<?>> get() {
		return (Map<String, IConfigVariable<?>>) getVariables();
	}

	@Override
	public void load(JsonElement element) {
		if(element.isJsonObject()) {
			Map<String, ? extends IConfigVariable<?>> variables = getVariables();
			element.getAsJsonObject().asMap().forEach((k, v) -> {
				if(variables.containsKey(k)) {
					variables.get(k).load(v);
				}
			});
		}
	}

	@SuppressWarnings("CodeBlock2Expr")
	@Override
	public Optional<JsonElement> save() {
		JsonObject obj = new JsonObject();
		getVariables().forEach((k, v) -> {
			v.save().ifPresent(val -> obj.add(k, val));
		});
		return Optional.of(obj);
	}
}
