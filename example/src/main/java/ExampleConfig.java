import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.celestialfault.celestialconfig.AbstractConfig;
import me.celestialfault.celestialconfig.variables.*;

import java.nio.file.Paths;
import java.util.List;

public class ExampleConfig extends AbstractConfig {
	protected ExampleConfig() {
		super(Paths.get("").toAbsolutePath().resolve("config.json"));
	}

	public final StringVariable string = new StringVariable("string", "Default");
	public final CharVariable character = new CharVariable("character", 'a');
	public final BooleanVariable bool = new BooleanVariable("boolean", true);
	public final FloatVariable floatVal = new FloatVariable("float", 1f);

	public final ArrayVariable<Integer> array = new ArrayVariable<>("array", List.of(6), JsonPrimitive::new, JsonElement::getAsInt);
	public final ExampleMap map = new ExampleMap();

	public static class ExampleMap extends MapVariable {
		public ExampleMap() {
			super("map");
		}

		public final FloatVariable floatVariable = new FloatVariable("float", 1f);
	}
}
