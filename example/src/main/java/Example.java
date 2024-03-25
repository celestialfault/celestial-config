import java.io.IOException;

public class Example {
	public static void main(String[] args) {
		ExampleConfig config = new ExampleConfig();
		try {
			config.load();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("String value: " + config.string.get());
		System.out.println("Character value: " + config.character.get());
		System.out.println("Boolean value: " + config.bool.get());
		System.out.println("Float value: " + config.floatVal.get());
		System.out.println("Map float value: " + config.map.floatVariable.get());
		System.out.println("Array value: " + config.array.get());

		config.string.set("New value");

		try {
			config.save();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
