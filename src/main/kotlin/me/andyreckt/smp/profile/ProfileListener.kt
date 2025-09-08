package me.andyreckt.smp.profile

import me.andyreckt.smp.SMPUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ProfileListener(val plugin: SMPUtils) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val profile = plugin.profileHandler.getProfile(player.uniqueId)

        if (profile == null) {
            val newProfile = SMPProfile(player.uniqueId)
            plugin.profileHandler.saveProfile(newProfile)
        }
    }
}