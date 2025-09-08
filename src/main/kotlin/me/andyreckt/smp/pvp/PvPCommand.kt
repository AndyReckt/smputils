package me.andyreckt.smp.pvp

import co.aikar.commands.BaseCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.CommandAlias
import me.andyreckt.smp.SMPUtils
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class PvPCommand(val plugin: SMPUtils) : BaseCommand() {

    @CommandAlias("pvp")
    fun onPvP(sender: Player) {
        val profile = plugin.profileHandler.getProfile(sender.uniqueId)
            ?: throw InvalidCommandArgument("Profile not found!")

        if (plugin.profileHandler.hasPvPTimer(sender.uniqueId)) {
            sender.sendMessage(text("You cannot toggle PvP while in combat!", NamedTextColor.RED))
            return
        }

        profile.pvpEnabled = !profile.pvpEnabled
        sender.sendMessage(text(
            "PvP is now ${if (profile.pvpEnabled) "enabled" else "disabled"}",
            if (profile.pvpEnabled) NamedTextColor.GREEN else NamedTextColor.RED
        ))

        plugin.profileHandler.saveProfile(profile)
    }

}