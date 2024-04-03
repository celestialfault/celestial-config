package me.celestialfault.celestialconfig;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Simple yet elegant wrapper around GSON for loading configuration files.</p>
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * public final class MyConfig extends AbstractConfig {
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
 *     // note that variables MUST be public, final, and non-static;
 *     // otherwise they will not be loaded or saved
 *     public final StringVariable string = new StringVariable("string_key", "Default value");
 * }
 *
 * // elsewhere:
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

	/**
	 * Controls the indentation of the saved file
	 *
	 * @see JsonWriter#setIndent(String)
	 */
	protected @NotNull String indent = "\t";
	/**
	 * A store of keys which were not accepted by any variables when loading the configuration file from disk
	 */
	protected final Map<String, JsonElement> unacceptedKeys = new HashMap<>();

	/**
	 * The path at which this configuration file will load and save its contents; this is set when calling
	 * {@link #AbstractConfig(Path) super(Path)} or {@link #AbstractConfig(Path, boolean) super(Path, boolean)}.
	 */
	protected final Path path;
	/**
	 * If {@code true}, {@link #load()} will instead call {@link #save()} if the {@link #path configuration file}
	 * doesn't exist already; this defaults to {@code true} if you don't turn this behavior off when
	 * {@link #AbstractConfig(Path, boolean) calling super()}
	 */
	protected final boolean createIfMissing;

	/**
	 * Create a new {@link AbstractConfig} with a config file path
	 *
	 * @apiNote This constructor will implicitly set {@link #createIfMissing} to {@code true}
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
	 * @param createIfMissing If {@code true}, {@link #load()} will instead run {@link #save()} if
	 *                        the provided {@code file} doesn't already exist
	 */
	protected AbstractConfig(Path file, boolean createIfMissing) {
		super();
		this.path = file;
		this.createIfMissing = createIfMissing;
	}

	/**
	 * Load the configuration file saved on disk into memory, or create a new one if it doesn't exist (and this
	 * configuration class wasn't created with the {@link #createIfMissing relevant setting} turned off).
	 */
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
			Map<String, ? extends IConfigVariable<?>> variables = getVariables();
			data.entrySet().forEach(entry -> {
				String k = entry.getKey();
				JsonElement v = entry.getValue();
				if(variables.containsKey(k)) {
					variables.get(k).load(v);
				} else {
					unacceptedKeys.put(k, v);
				}
			});
		}
	}

	/**
	 * Save all variables to the configuration file on disk
	 */
	@SuppressWarnings("CodeBlock2Expr")
	public void save() throws IOException {
		File configFile = path.toFile();
		try(FileWriter writer = new FileWriter(configFile); JsonWriter jsonWriter = new JsonWriter(writer)) {
			jsonWriter.setIndent(indent);
			JsonObject object = new JsonObject();
			getVariables().forEach((name, variable) -> {
				variable.save().ifPresent(v -> object.add(name, v));
			});
			unacceptedKeys.entrySet().stream()
				.filter(entry -> !object.has(entry.getKey()))
				.forEach(entry -> object.add(entry.getKey(), entry.getValue()));
			ADAPTER.write(jsonWriter, object);
		}
	}
}
