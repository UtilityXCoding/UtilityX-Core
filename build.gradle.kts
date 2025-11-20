import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "xyz.blackdev"
version = "0725"

repositories {
    mavenCentral()

    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        name = "xenondevsReleases"
        url = uri("https://repo.xenondevs.xyz/releases")
    }

    maven {
        name = "craftsblock"
        url = uri("https://repo.craftsblock.de/releases")
    }
    maven {
        name = "utilityxRepositoryReleases"
        url = uri("https://repo.utilityx.de/releases")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    implementation("xyz.xenondevs.invui:invui:1.47")
    implementation("de.craftsblock.craftscore:json:3.8.10")
    implementation("de.utilityx:utilityx-api:0.0.1")
}

tasks {
    runServer {
        minecraftVersion("1.21.10")
    }
}

val targetJavaVersion = 21

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.named<ProcessResources>("processResources") {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    archiveBaseName.set("UtilityX-Core")
    archiveVersion.set(project.version.toString())
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("UtilityX-Core")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("all")
}