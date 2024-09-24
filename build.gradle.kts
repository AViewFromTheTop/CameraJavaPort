import groovy.xml.XmlSlurper
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import java.io.FileInputStream
import java.nio.file.Files
import java.util.Properties
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub
import java.io.FileNotFoundException
import java.net.URL

buildscript {
	repositories {
		gradlePluginPortal()
	}
	dependencies {
		classpath("org.kohsuke:github-api:+")
	}
}

plugins {
	id("fabric-loom") version("+")
	id("org.quiltmc.gradle.licenser") version("+")
	id("org.ajoberstar.grgit") version("+")
	id("com.modrinth.minotaur") version("+")
	`maven-publish`
	eclipse
	idea
	`java-library`
	java
}

val githubActions: Boolean = System.getenv("GITHUB_ACTIONS") == "true"
val licenseChecks: Boolean = githubActions

val minecraft_version: String by project
val quilt_mappings: String by project
val parchment_mappings: String by project
val loader_version: String by project

val mod_id: String by project
val mod_version: String by project
val protocol_version: String by project
val mod_loader: String by project
val maven_group: String by project
val archives_base_name: String by project

val fabric_api_version: String by project
val modmenu_version: String by project
val frozenlib_version: String by project

val local_frozenlib = findProject(":FrozenLib") != null

base {
	archivesName = archives_base_name
}

version = getModVersion()
group = maven_group

val release = findProperty("releaseType") == "stable"

val datagen by sourceSets.registering {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

loom {
	runtimeOnlyLog4j.set(true)

	mixin {
		defaultRefmapName.set("mixins.$mod_id.refmap.json")
	}

	accessWidenerPath.set(file("src/main/resources/$mod_id.accesswidener"))
	interfaceInjection {
		// When enabled, injected interfaces from dependencies will be applied.
		enableDependencyInterfaceInjection.set(false)
	}
}

val includeModImplementation by configurations.creating
val includeImplementation by configurations.creating

configurations {
	include {
		extendsFrom(includeImplementation)
		extendsFrom(includeModImplementation)
	}
	implementation {
		extendsFrom(includeImplementation)
	}
	modImplementation {
		extendsFrom(includeModImplementation)
	}
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	maven("https://jitpack.io")
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven") {
				name = "Modrinth"
			}
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
	maven("https://maven.terraformersmc.com") {
		content {
			includeGroup("com.terraformersmc")
		}
	}

	maven("https://maven.shedaniel.me/")
	maven("https://maven.minecraftforge.net/")
	maven("https://maven.parchmentmc.org")
	maven("https://maven.quiltmc.org/repository/release") {
		name = "Quilt"
	}

	flatDir {
		dirs("libs")
	}
	mavenCentral()
}

dependencies {
	// To change the versions, see the gradle.properties file
	minecraft("com.mojang:minecraft:$minecraft_version")
	mappings(loom.layered {
		// please annoy treetrain if this doesnt work
		mappings("org.quiltmc:quilt-mappings:$quilt_mappings:intermediary-v2")
		//parchment("org.parchmentmc.data:parchment-$parchment_mappings@zip")
		officialMojangMappings {
			nameSyntheticMembers = false
		}
	})
	modImplementation("net.fabricmc:fabric-loader:$loader_version")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

	// Mod Menu
	modCompileOnly("com.terraformersmc:modmenu:$modmenu_version")

	/*
	// FrozenLib
	if (local_frozenlib) {
		api(project(":FrozenLib", configuration = "namedElements"))
		modCompileOnly(project(":FrozenLib"))?.let { include(it) }
	} else
		modApi("maven.modrinth:frozenlib:$frozenlib_version")?.let { include(it) }
	 */
}

tasks {
	processResources {
		val properties = mapOf(
			"mod_id" to mod_id,
			"version" to version,
			"minecraft_version" to "~1.21-",//>=minecraft_version,

			"fabric_api_version" to ">=$fabric_api_version",
			"frozenlib_version" to ">=${frozenlib_version.split('-').firstOrNull()}-"
		)

		properties.forEach { (a, b) -> inputs.property(a, b) }

		filesNotMatching(
			listOf(
				"**/*.java",
				"**/sounds.json",
				"**/lang/*.json",
				"**/.cache/*",
				"**/*.accesswidener",
				"**/*.nbt",
				"**/*.png",
				"**/*.ogg",
				"**/*.mixins.json"
			)
		) {
			expand(properties)
		}
	}

	register("javadocJar", Jar::class) {
		dependsOn(javadoc)
		archiveClassifier.set("javadoc")
		from(javadoc.get().destinationDir)
	}

	register("sourcesJar", Jar::class) {
		dependsOn(classes)
		archiveClassifier.set("sources")
		from(sourceSets.main.get().allSource)
	}

	withType(JavaCompile::class) {
		options.encoding = "UTF-8"
		// Minecraft 1.20.5 (24w14a) upwards uses Java 21.
		options.release.set(21)
		options.isFork = true
		options.isIncremental = true
	}

	withType(Test::class) {
		maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
	}
}

val applyLicenses: Task by tasks
val test: Task by tasks
val runClient: Task by tasks

val remapJar: Task by tasks
val sourcesJar: Task by tasks
val javadocJar: Task by tasks

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21

	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

artifacts {
	archives(sourcesJar)
	archives(javadocJar)
}

fun getModVersion(): String {
	var version = "$mod_version-$mod_loader+$minecraft_version"

	if (release != null && !release) {
		//version += "-unstable"
	}

	return version
}
