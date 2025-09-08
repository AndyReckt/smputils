import java.util.Properties

plugins {
    kotlin("jvm") version "2.2.10"
    id("com.gradleup.shadow") version "9.1.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("com.gorylenko.gradle-git-properties") version "2.5.3"
}

group = "me.andyreckt"
version = "0.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()

    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }

    maven("https://repo.codemc.io/repository/creatorfromhell/") {
        name = "vault-repo"
    }

    maven("https://repo.j4c0b3y.net/public/") {
        name = "j4c0b3y-repo"
    }

    maven("https://repo.aikar.co/content/groups/aikar/") {
        name = "aikar-repo"
    }

    maven("https://repo.mincats.eu/mirrors")
}

dependencies {
    //lombok
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    compileOnly("org.projectlombok:lombok:1.18.34")

    //paper
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
    //kotlin sdk
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //vault
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.15")
    //google
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.google.guava:guava:33.4.8-jre")
    //configapi
    implementation("net.j4c0b3y:ConfigAPI-bukkit:1.2.6")
    //mongodb
    implementation("org.mongodb:mongodb-driver-reactivestreams:5.1.2")
    implementation("io.projectreactor:reactor-core:3.6.8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.3.0-RC1")
    //acf
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

tasks.shadowJar {
    relocate("co.aikar.commands", "me.andyreckt.reloactions.acf")
    relocate("co.aikar.locales", "me.andyreckt.reloactions.locales")

    relocate("com.google.gson", "me.andyreckt.reloactions.gson")
    relocate("com.google.common", "me.andyreckt.reloactions.guava")
    relocate("org.bson", "me.andyreckt.reloactions.bson")
    relocate("com.mongodb", "me.andyreckt.reloactions.mongodb")
    relocate("org.reactivestreams", "me.andyreckt.reloactions.reactive")
    relocate("reactor", "me.andyreckt.reloactions.reactor")

    relocate("net.j4c0b3y", "me.andyreckt.reloactions.jacob")
    relocate("dev.dejvokep", "me.andyreckt.reloactions.dejvokep")
}

tasks {
    runServer {
        downloadPlugins {
            //vault
            modrinth("vaultunlocked", "2.15.1")

            //viaversion
            modrinth("viaversion", "5.5.0-SNAPSHOT+817")
            modrinth("viabackwards", "5.4.3-SNAPSHOT+478")

            //essentialsx
            github("Euphillya", "Essentials-Folia", "build-folia-patches-72", "EssentialsX-2.22.0-dev+33-c893f68-Folia.jar")
        }

        minecraftVersion("1.21.8")
    }
}


gitProperties  {
    extProperty = "gitProps"
    dateFormat = "dd-MM-yyyy-HH:mm"
}

tasks.generateGitProperties {
    outputs.upToDateWhen { false }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    dependsOn("generateGitProperties")
    outputs.upToDateWhen { false }

    doFirst {
        // Access git properties here, after generateGitProperties has run
        val gitProps = project.extra["gitProps"] as Map<String, String>

        val gitCommitId = gitProps["git.commit.id.abbrev"] ?: "unknown"
        val gitCommitTime = gitProps["git.commit.time"] ?: "unknown"
        val gitBranch = gitProps["git.branch"] ?: "unknown"
        val fullVersion = "$version-$gitCommitId-${gitCommitTime.replace(":", "-")}"

        val props = mapOf(
            "baseVersion" to version,
            "version" to fullVersion,
            "gitCommitId" to gitCommitId,
            "gitCommitTime" to gitCommitTime,
            "gitBranch" to gitBranch
        )

        println("\nGit Properties Available:")
        gitProps.forEach { (key, value) ->
            println("  $key = $value")
        }
        println("\nTemplate Properties:")
        props.forEach { (key, value) ->
            println("  $key = $value")
        }

        // Set properties for template expansion
        project.extra["templateProps"] = props
    }

    filteringCharset = "UTF-8"

    filesMatching("paper-plugin.yml") {
        val props = project.extra["templateProps"] as Map<String, String>
        expand(props)
    }
}