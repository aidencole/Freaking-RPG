plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "dev.freakingrpg"
version = "0.1.0-SNAPSHOT"

val paperVersion: String by project

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperVersion}.build.+")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.processResources {
    val props = mapOf("version" to version)
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.jar {
    archiveBaseName.set("FreakingRPG")
}

tasks {
    runServer {
        minecraftVersion(paperVersion)
    }
}
