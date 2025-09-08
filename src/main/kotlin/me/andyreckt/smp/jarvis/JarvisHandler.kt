package me.andyreckt.smp.jarvis

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import me.andyreckt.smp.util.openrouter.OpenRouterClient
import me.andyreckt.smp.util.openrouter.constants.Models
import me.andyreckt.smp.SMPUtils
import me.andyreckt.smp.jarvis.data.ChatMessage

class JarvisHandler(val plugin: SMPUtils, val API_KEY: String) {

    lateinit var client: OpenRouterClient
    lateinit var jarvis: JarvisService

    lateinit var cache: Cache<Long, ChatMessage>

    fun enable() {
        cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build()

        client = OpenRouterClient.create(
            apiKey = API_KEY,
            appName = "Jarvis (Risu's AI)",
        )

        client.setDefaultModel(Models.GEMINI_2_5_FLASH)

        jarvis = JarvisService(client)
        plugin.server.pluginManager.registerEvents(JarvisListener(this), plugin)
    }
}