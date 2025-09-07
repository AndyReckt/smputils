plugins {
    kotlin("jvm") version "2.2.10"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

group = "me.andyreckt"
version = "0.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()

    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    paperweight.foliaDevBundle("1.21.8-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

runPaper.folia.registerTask()

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.8")

        downloadPlugins {
            //vault
            modrinth("vaultunlocked", "2.15.1")

            //viaversion
            modrinth("viaversion", "5.5.0-SNAPSHOT+817")
            modrinth("viabackwars", "5.5.0-SNAPSHOT+817")

            //towny
            url("https://files.exah.cc/r/NsGWdu.jar")
            url("https://files.exah.cc/r/2TwgCi.jar")

            //
        }
    }


}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
