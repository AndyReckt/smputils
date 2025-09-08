package me.andyreckt.smp.pvp


import me.andyreckt.smp.SMPUtils
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PvPListener(val plugin: SMPUtils) : Listener {

    @EventHandler
    fun combatEvent(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val entity = event.entity

        if (damager !is Player || entity !is Player) return

        fun hasPvPEnabled(player: Player): Boolean {
            val profile = plugin.profileHandler.getProfile(player.uniqueId) ?: return false
            return profile.pvpEnabled
        }

        if (!hasPvPEnabled(damager)) {
            event.isCancelled = true
            damager.sendMessage(text("You cannot PvP while your PvP is disabled!", NamedTextColor.RED))
            return
        }

        if (!hasPvPEnabled(entity)) {
            event.isCancelled = true
            damager.sendMessage(text("${entity.name} has their PvP disabled", NamedTextColor.RED))
            return
        }

        plugin.profileHandler.applyPvPTimer(damager.uniqueId)
        plugin.profileHandler.applyPvPTimer(entity.uniqueId)
    }
}