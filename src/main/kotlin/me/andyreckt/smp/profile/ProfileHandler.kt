package me.andyreckt.smp.profile

import com.google.common.cache.CacheBuilder
import me.andyreckt.smp.SMPUtils
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ProfileHandler(val plugin: SMPUtils) {
    var profiles: ProfileRepository
    val pvpCache = CacheBuilder.newBuilder()
        .expireAfterWrite(15.seconds.toJavaDuration())
        .build<UUID, Long>()


    init {
        profiles = ProfileRepository(plugin.mongoHandler.database.getCollection("profiles"))
    }

    fun getProfile(uuid: UUID): SMPProfile? {
        return profiles.getFromCache(uuid)
    }

    fun saveProfile(profile: SMPProfile) {
        profiles.save(profile)
    }

    fun applyPvPTimer(uuid: UUID) {
        pvpCache.put(uuid, System.currentTimeMillis() + 15_000)
    }

    fun hasPvPTimer(uuid: UUID): Boolean {
        val time = pvpCache.getIfPresent(uuid) ?: return false
        if (System.currentTimeMillis() > time) {
            pvpCache.invalidate(uuid)
            return false
        }
        return true
    }
}