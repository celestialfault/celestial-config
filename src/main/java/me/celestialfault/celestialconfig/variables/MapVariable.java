package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.celestialfault.celestialconfig.IConfigVariable;
import me.celestialfault.celestialconfig.impl.VariableScanner;

import java.util.Map;
import java.util.Optional;

/**
 * Map variable; this is intended to be used as a subclass.
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * class MyConfig extends AbstractConfig {
 *     public final MyMap map = new MyMap();
 *
 *     public static class MyMap extends MapVariable {
 *         public MyMap() {
 *             super("map");
 *         }
 *
 *         public final BooleanVariable boolean = new BooleanVariable("bool", true);
 *     }
 * }
 * }</pre>
 */
public abstract class MapVariable extends VariableScanner implements IConfigVariable<Map<String, IConfigVariable<?>>> {
	private final String key;

	public MapVariable(String key) {
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

	@Override
	public Optional<JsonElement> save() {
		JsonObject obj = new JsonObject();
		getVariables().forEach((k, v) -> {
			v.save().ifPresent(val -> obj.add(k, val));
		});
		return Optional.of(obj);
	}
}
