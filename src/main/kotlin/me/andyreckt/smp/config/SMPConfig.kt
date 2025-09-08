package me.andyreckt.smp.config

import me.andyreckt.smp.SMPUtils
import net.j4c0b3y.api.config.StaticConfig
import java.io.File

class SMPConfig(plugin: SMPUtils) : StaticConfig(File(plugin.dataFolder, "config.yml"), plugin.configHandler) {
    companion object {
        @JvmStatic val MONGO_URI = "mongodb://localhost:27017"
    }
}