package me.andyreckt.smp.util.openrouter.config

data class ClientConfig(
    val connectTimeoutSeconds: Long = 30L,
    val readTimeoutSeconds: Long = 300L, // 5 minutes for complex tasks
    val streamReadTimeoutSeconds: Long = 600L, // 10 minutes for streaming
    val maxRetries: Int = 3,
    val baseRetryDelayMs: Long = 1000L,
    val maxRetryDelayMs: Long = 60000L
)