package me.andyreckt.smp.profile

import com.mongodb.reactivestreams.client.MongoCollection
import me.andyreckt.smp.util.mongo.MongoMapRepository
import me.andyreckt.smp.util.mongo.MongoRepository
import org.bson.Document
import java.util.UUID

class ProfileRepository(collection: MongoCollection<Document>)
    : MongoMapRepository<UUID, SMPProfile>(SMPProfile::class.java, collection) {

    init {
        this.allEntriesFromDatabase.subscribe {
            cache[it.uuid] = it
        }
    }

    fun save(profile: SMPProfile) {
        saveToDatabase(profile.uuid, profile)
        cache[profile.uuid] = profile
    }
}