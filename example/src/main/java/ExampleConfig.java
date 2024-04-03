import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.AbstractConfig;
import me.celestialfault.celestialconfig.variables.*;

import java.nio.file.Paths;
import java.util.List;

public class ExampleConfig extends AbstractConfig {
	protected ExampleConfig() {
		// loads and saves to config.json in the current directory
		super(Paths.get("").toAbsolutePath().resolve("config.json"));
	}

	public final StringVariable string = new StringVariable("string", "Default");
	public final CharVariable character = new CharVariable("character", 'a');
	public final BooleanVariable bool = new BooleanVariable("boolean", true);
	public final FloatVariable floatVal = new FloatVariable("float", 1f);

	public final ArrayVariable<Integer> array = new ArrayVariable<>("array", List.of(6), JsonPrimitive::new, JsonElement::getAsInt);
	public final ExampleMap map = new ExampleMap();

	// note that you don't access this map from this subclass, but from the `map` field above!
	public static class ExampleMap extends VariableMap {
		private ExampleMap() {
			super("map");
		}

		public final FloatVariable floatVariable = new FloatVariable("float", 1f);
	}
}
