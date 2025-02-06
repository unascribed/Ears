import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URL

plugins {
	id("maven-publish")
	id("fabric-loom") version "1.8-SNAPSHOT"
	id("babric-loom-extension") version "1.8.5"
	id ("com.github.johnrengelman.shadow") version "8.1.1"
}

//noinspection GroovyUnusedAssignment
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

base.archivesName = project.properties["archives_base_name"] as String
version = file("../version.txt").readText().trim()+file("version-suffix.txt").readText().trim()
group = project.properties["maven_group"] as String

loom {
//	accessWidenerPath = file("src/main/resources/examplemod.accesswidener")

	runs {
		// If you want to make a testmod for your mod, right click on src, and create a new folder with the same name as source() below.
		// Intellij should give suggestions for testmod folders.
		register("testClient") {
			source("test")
			client()
		}
		register("testServer") {
			source("test")
			server()
		}
	}
}

repositories {
	maven("https://maven.glass-launcher.net/snapshots/")
	maven("https://maven.glass-launcher.net/releases/")
	maven("https://maven.glass-launcher.net/babric")
	maven("https://maven.minecraftforge.net/")
	maven("https://jitpack.io/")
	mavenCentral()
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:b1.7.3")
	mappings("net.glasslauncher:biny:${ if ((project.properties["yarn_mappings"] as String) == "%s") "b1.7.3+4cbd9c8" else project.properties["yarn_mappings"] }:v2")

	modImplementation("babric:fabric-loader:${project.properties["loader_version"]}")
	modImplementation("com.github.calmilamsy:ModMenu:${project.properties["modmenu_version"]}")

	implementation(files("../common/build/libs/ears-common-mixin-vlegacy.jar"))
}

configurations.all {
	exclude(group = "org.ow2.asm", module = "asm-debug-all")
	exclude(group = "org.ow2.asm", module = "asm-all")
}

tasks.withType<ProcessResources> {
	inputs.property("version", project.properties["version"])
	inputs.property("modid", project.properties["mod_id"])

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.properties["version"], "modid" to project.properties["mod_id"]))
	}
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	doFirst {
		exec {
			workingDir(".")
			commandLine("../common/replace-version.sh", "src/main/java/com/unascribed/ears/EarsPlatformVersion.java")
		}
		
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

tasks.withType<Jar> {
	from(zipTree("../common/build/libs/ears-common-mixin-vlegacy.jar"))
	from("LICENSE") {
		rename { "${it}_${project.properties["archivesBaseName"]}" }
	}
}

publishing {
	repositories {
		mavenLocal()
		if (project.hasProperty("my_maven_username")) {
			maven {
				url = URI("https://maven.example.com")
				credentials {
					username = "${project.properties["my_maven_username"]}"
					password = "${project.properties["my_maven_password"]}"
				}
			}
		}
	}

	publications {
		register("mavenJava", MavenPublication::class) {
			artifactId = project.properties["archives_base_name"] as String
			from(components["java"])
		}
	}
}

fun File.cd(subDir: String): File {
	return File(this, subDir)
}

fun readInput(): String {
	return BufferedReader(InputStreamReader(System.`in`)).readLine()
}