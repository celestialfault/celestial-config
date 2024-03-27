package me.celestialfault.celestialconfig.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.celestialfault.celestialconfig.IConfigVariable;
import me.celestialfault.celestialconfig.impl.VariableScanner;

import java.util.Map;
import java.util.Optional;

/**
 * A map of differing variable types; this is designed to be used as a subclass with variables as fields, similarly
 * to an {@link me.celestialfault.celestialconfig.AbstractConfig AbstractConfig}.
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * public final class MyConfig extends AbstractConfig {
 *     // this field is how you access the variables contained in your map!
 *     public final MyMap map = new MyMap();
 *
 *     public static class MyMap extends VariableMap {
 *         private MyMap() {
 *             super("map");
 *         }
 *
 *         // access with MyConfig#map.boolean
 *         public final BooleanVariable boolean = new BooleanVariable("bool", true);
 *     }
 * }
 * }</pre>
 *
 * This could also be used as a type in {@link ArrayVariable arrays}:
 *
 * <pre>{@code
 * public final class MyMap extends VariableMap {
 *     public MyMap(JsonElement element) {
 *         super("");
 *         this.load(element);
 *     }
 *
 *     public final StringVariable name = new StringVariable("name", null);
 *     public final DoubleVariable value = new DoubleVariable("value", 1);
 * }
 *
 * public final ArrayVariable<MyMap> arrayMap = new ArrayVariable<>("array_map",
 *          v -> v.save().orElse(null), MyMap::new);
 * }</pre>
 */
public abstract class VariableMap extends VariableScanner implements IConfigVariable<Map<String, IConfigVariable<?>>> {
	private final String key;

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
