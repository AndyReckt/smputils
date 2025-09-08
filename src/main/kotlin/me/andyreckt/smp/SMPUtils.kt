package me.andyreckt.smp

import co.aikar.commands.PaperCommandManager
import me.andyreckt.smp.config.SMPConfig
import me.andyreckt.smp.jarvis.JarvisHandler
import me.andyreckt.smp.mongo.MongoHandler
import me.andyreckt.smp.profile.ProfileHandler
import me.andyreckt.smp.profile.ProfileListener
import me.andyreckt.smp.pvp.PvPCommand
import me.andyreckt.smp.pvp.PvPListener
import net.j4c0b3y.api.config.platform.bukkit.BukkitConfigHandler
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import java.util.logging.Logger

class SMPUtils : JavaPlugin() {

    lateinit var commandHandler: PaperCommandManager

    lateinit var mongoHandler: MongoHandler
    lateinit var configHandler: BukkitConfigHandler

    lateinit var eco: Economy
    lateinit var config: SMPConfig

    lateinit var profileHandler: ProfileHandler
    lateinit var jarvisHandler: JarvisHandler

    override fun onEnable() {
        instance = this
        this.logger.info("Enabling SMP Utils")

        configHandler = BukkitConfigHandler(this.logger)

        commandHandler = PaperCommandManager(this)
        commandHandler.enableUnstableAPI("help")

        try {
            config = SMPConfig(this)
            config.load()

            loadMongo()
            loadProfiles()

            loadEconomy()
            loadPvPChecks()
            loadJarvis()
        } catch (ex: Exception) {
            this.logger.severe("Error while loading SMP Utils, ${ex.message}")
            ex.printStackTrace()

            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        this.logger.info("Finished loading SMP Utils")
    }

    override fun onDisable() {
        this.logger.info("Disabling SMP Utils")
    }


    fun loadEconomy() {
        val economyServiceProvider = this.server.servicesManager.getRegistration<Economy>(Economy::class.java)
            ?: throw NullPointerException("Economy service not found")

        eco = economyServiceProvider.provider
    }

    fun loadPvPChecks() {
        this.server.pluginManager.registerEvents(PvPListener(this), this)
        commandHandler.registerCommand(PvPCommand(this))
    }

    fun loadMongo() {
        Logger.getLogger("org.mongodb").level = Level.WARNING
        Logger.getLogger("com.mongodb").level = Level.WARNING
        Logger.getLogger("me.andyreckt.relocations.mongodb").level = Level.WARNING

        mongoHandler = MongoHandler(SMPConfig.MONGO_URI)
        mongoHandler.connect()
    }

    fun loadJarvis() {
        jarvisHandler = JarvisHandler(this, SMPConfig.OPENROUTER_API_KEY)
        jarvisHandler.enable()
    }

    fun loadProfiles() {
        profileHandler = ProfileHandler(this)
        this.server.pluginManager.registerEvents(ProfileListener(this), this)
    }

    companion object {
        @JvmStatic lateinit var instance: SMPUtils
    }
}
