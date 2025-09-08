package me.andyreckt.smp

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository



@Suppress("UnstableApiUsage")
class SMPLoader : PluginLoader {

    override fun classloader(classpathBuilder: PluginClasspathBuilder) {
        val resolver = MavenLibraryResolver()

        resolver.addRepository(
            RemoteRepository.Builder(
                "central",
                "default",
                MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
            ).build()
        )

        resolver.addRepository(
            RemoteRepository.Builder(
                "j4c0b3y-repo",
                "default",
                "https://repo.j4c0b3y.net/public/"
            ).build()
        )

        resolver.addRepository(
            RemoteRepository.Builder(
                "aikar-repo",
                "default",
                "https://repo.aikar.co/content/groups/aikar/"
            ).build()
        )

        resolver.addRepository(
            RemoteRepository.Builder(
                "mincats",
                "default",
                "https://repo.mincats.eu/mirrors"
            ).build()
        )

        resolver.addRepository(
            RemoteRepository.Builder(
                "jitpack.io",
                "default",
                "https://jitpack.io"
            ).build()
        )

        resolver.addDependency(Dependency(DefaultArtifact("com.google.code.gson:gson:2.13.1"), null))
        resolver.addDependency(Dependency(DefaultArtifact("com.google.guava:guava:33.4.8-jre"), null))
        resolver.addDependency(Dependency(DefaultArtifact("net.j4c0b3y:ConfigAPI-bukkit:1.2.6"), null))
        resolver.addDependency(Dependency(DefaultArtifact("org.mongodb:mongodb-driver-reactivestreams:5.1.2"), null))
        resolver.addDependency(Dependency(DefaultArtifact("io.projectreactor:reactor-core:3.6.8"), null))
        resolver.addDependency(Dependency(DefaultArtifact("io.projectreactor.kotlin:reactor-kotlin-extensions:1.3.0-RC1"), null))
        resolver.addDependency(Dependency(DefaultArtifact("co.aikar:acf-paper:0.5.1-SNAPSHOT"), null))
        resolver.addDependency(Dependency(DefaultArtifact("com.squareup.okhttp3:okhttp:4.12.0"), null))
        resolver.addDependency(Dependency(DefaultArtifact("com.squareup.okhttp3:logging-interceptor:4.12.0"), null))

        classpathBuilder.addLibrary(resolver);
    }

}