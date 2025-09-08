package me.andyreckt.smp.jarvis

import com.google.common.cache.CacheBuilder
import io.papermc.paper.event.player.AsyncChatEvent
import me.andyreckt.smp.jarvis.data.ChatMessage
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

class JarvisListener(val handler: JarvisHandler) : Listener {

    @EventHandler
    fun onChatMessage(event: AsyncChatEvent) {
        val message = event.message().toString()
        val player = event.player
        val timestamp = System.currentTimeMillis()

        handler.cache.put(timestamp, ChatMessage(
            player.uniqueId.toString(), player.name,
            player.displayName,message,
            timestamp, false
        ))


        if (message.contains("@jarvis")) {
            if (!player.hasPermission("smp.jarvis")) {
                player.sendMessage {
                    text("You do not have permission to use Jarvis", NamedTextColor.RED).appendNewline()
                        .append { text("Please buy a rank at https://comick.craftingstore.net/", NamedTextColor.YELLOW) }
                }
                return
            }

            ForkJoinPool.commonPool().execute {
                val response = handler.jarvis.getAIResponse(handler.cache.asMap().values, message)
                Bukkit.getOnlinePlayers().forEach { player ->
                    val component = text("Jarvis: ", NamedTextColor.AQUA)
                        .append(text(response, NamedTextColor.WHITE))

                    player.sendMessage(component)
                    Bukkit.getConsoleSender().sendMessage(component)
                }
            }
        }
    }
}