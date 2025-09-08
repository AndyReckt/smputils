package me.andyreckt.smp.jarvis.data

data class ChatMessage(
    val authorId: String,
    val authorUsername: String,
    val authorDisplayname: String,
    val content: String,
    val timestamp: Long,
    val isBot: Boolean = false,
)