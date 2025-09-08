package me.andyreckt.smp.util.other


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import me.andyreckt.smp.util.json.adapter.ExclusionStrategyAdapter
import me.andyreckt.smp.util.json.adapter.PostProcessAdapter
import me.andyreckt.smp.util.json.adapter.UUIDAdapter
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

object Statics {
    const val PERMANENT = -1L

    @JvmStatic
    fun hash(input: String): String {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
            return Base64.getEncoder().encodeToString(hash)
        } catch (e: Exception) {
            throw IllegalStateException("Hashing Failed for ($input)")
        }
    }

    @JvmField
    var GSON: Gson = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .registerTypeAdapter(UUID::class.java, UUIDAdapter())
        .setExclusionStrategies(ExclusionStrategyAdapter())
        .registerTypeAdapterFactory(PostProcessAdapter())
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .serializeNulls()
        .create()

    @JvmStatic
    fun registerTypeAdapter(baseClass: Class<*>, typeAdapter: Any) {
        useGsonBuilderThenRebuild {
            registerTypeAdapter(baseClass, typeAdapter)
        }
    }

    @JvmStatic
    fun useGsonBuilderThenRebuild(builder: GsonBuilder.() -> GsonBuilder) {
        GSON = builder(GSON.newBuilder()).create()
    }
}
