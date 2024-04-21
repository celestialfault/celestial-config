import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
	idea
	java
	id("maven-publish")
	kotlin("jvm") version "1.9.23"
	id("org.jetbrains.dokka") version "1.9.20"
}

group = "me.celestialfault"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains:annotations:24.0.0")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation(kotlin("reflect"))
}

java {
	toolchain.languageVersion = JavaLanguageVersion.of(8)
	withSourcesJar()
}

publishing {
	repositories {
		maven {
			name = "celestialfault"
			url = uri("https://maven.odinair.xyz/releases")
			credentials {
				username = (project.findProperty("maven.username") ?: System.getenv("MAVEN_NAME")) as String
				password = (project.findProperty("maven.secret") ?: System.getenv("MAVEN_SECRET")) as String
			}
		}
	}

	publications {
		create<MavenPublication>("celestial-config") {
			from(components["java"])
		}
	}
}

tasks.withType<DokkaTask>().configureEach {
	dokkaSourceSets {
		named("main") {
			moduleName.set("Celestial Config")
			includes.from("Module.md")
			sourceLink {
				localDirectory.set(file("src/main/kotlin"))
				remoteUrl.set(URL("https://github.com/celestialfault/celestial-config/blob/master/src/main/kotlin"))
				remoteLineSuffix.set("#L")
			}
		}
	}
}

tasks.register<Jar>("dokka") {
	dependsOn(tasks.dokkaHtml)
	from(tasks.dokkaHtml.flatMap { it.outputDirectory })
	archiveClassifier.set("javadoc")
}

tasks.build.configure {
	dependsOn(tasks["dokka"])
}
