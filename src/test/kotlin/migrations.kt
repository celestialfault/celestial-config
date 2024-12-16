import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.migrations.Migrations
import java.nio.file.Paths

// note: migrations run before anything is loaded, and as such receive the underlying JsonObject;
// the provided JsonObject must be mutated in-place to apply migrations.
val migrations = Migrations.create {
	// simple key rename
	add { it.add("new", it.remove("old")) }
	// add a new property that previously didn't exist
	add { it.addProperty("other", "a new key") }
}

class PreMigrationTest : AbstractConfig(Paths.get(".", "tests", "migration.json")) {
	val oldKey by Property.of<String>("old", "hello world")
}

class MigrationTest : AbstractConfig(Paths.get(".", "tests", "migration.json"), migrations = migrations) {
	val newKey by Property.ofNullable<String>("new")
	val otherKey by Property.ofNullable<String>("other")
}

fun main() {
	val pre = PreMigrationTest()
	pre.save()
	println(pre)
	val post = MigrationTest()
	require(post.configVersion == 2) { "Config didn't correctly initialize the version!" }
	post.load()
	println(post)
	require(post.configVersion == 2) { "Migrations did not apply correctly!" }
	require(post.newKey == pre.oldKey) { "Migration #1 did not apply!" }
	require(post.otherKey == "a new key") { "Migration #2 did not apply!" }
	post.save()
}
