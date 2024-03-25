package me.celestialfault.celestialconfig;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import me.celestialfault.celestialconfig.impl.VariableScanner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * <p>Simple yet elegant wrapper around GSON for loading configuration files.</p>
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * import me.celestialfault.celestialconfig.AbstractConfig;
 * import me.celestialfault.celestialconfig.variables.StringVariable;
 * import java.nio.file.Path;
 * import java.nio.file.Paths;
 *
 * public class MyConfig extends AbstractConfig {
 *     public MyConfig() {
 *         // create a Path based on where your program is running
 *         super(Paths.get("").getAbsolutePath().resolve("config.json"));
 *     }
 *
 *     public MyConfig(Path path) {
 *         // or use a Path you have from elsewhere
 *         super(path);
 *     }
 *
 *     // note that variables MUST be final and non-static,
 *     // otherwise they will not be loaded or saved
 *     public final StringVariable string = new StringVariable("string_key", "Default value");
 * }
 *
 * // ... elsewhere:
 *
 * MyConfig config = new MyConfig();
 * // load from disk
 * config.load();
 *
 * config.string.get(); // -> "Default value"
 * config.string.set("New value");
 * config.string.get(); // -> "New value"
 *
 * // now write the changed variable to disk
 * config.save();
 * }</pre>
 */
public abstract class AbstractConfig extends VariableScanner {

	private static final TypeAdapter<JsonObject> ADAPTER = new Gson().getAdapter(JsonObject.class);

	protected final Path path;
	protected final boolean createIfMissing;

	/**
	 * Create a new {@link AbstractConfig} with a config file path
	 *
	 * @param file The path to where your config file should reside on disk
	 */
	protected AbstractConfig(Path file) {
		this(file, true);
	}

	/**
	 * Create a new {@link AbstractConfig}, controlling whether the config file should be created
	 * if it doesn't already exist on disk.
	 *
	 * @param file            The path to where your config file should reside on disk
	 * @param createIfMissing If {@code true}, {@link #load()} will call {@link #save()} if
	 *                        the provided {@code file} doesn't already exist
	 */
	protected AbstractConfig(Path file, boolean createIfMissing) {
		super();
		this.path = file;
		this.createIfMissing = createIfMissing;
	}

	public void load() throws IOException {
		File configFile = path.toFile();
		if(!configFile.exists()) {
			if(createIfMissing) {
				save();
			}
			return;
		}

		try(FileReader reader = new FileReader(configFile)) {
			JsonObject data = ADAPTER.fromJson(reader);
			getVariables().forEach((name, variable) -> {
				if(data.has(name)) {
					variable.load(data.get(name));
				}
			});
		}
	}

	@SuppressWarnings("CodeBlock2Expr")
	public void save() throws IOException {
		File configFile = path.toFile();
		try(FileWriter writer = new FileWriter(configFile); JsonWriter jsonWriter = new JsonWriter(writer)) {
			jsonWriter.setIndent("\t");
			JsonObject object = new JsonObject();
			getVariables().forEach((name, variable) -> {
				variable.save().ifPresent(v -> object.add(name, v));
			});
			ADAPTER.write(jsonWriter, object);
		}
	}
}
