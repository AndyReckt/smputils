package me.andyreckt.smp.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.codecs.LongCodec
import org.bson.codecs.configuration.CodecRegistries
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.TimeUnit

class MongoHandler(val uri: String) {
    lateinit var client: MongoClient
    lateinit var database: MongoDatabase

    fun connect() {
        client = MongoClients.create(MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(uri))
            .applicationName("comick-smp-plugin")
            .retryWrites(true)
            .applyToConnectionPoolSettings {
                it.maxConnectionIdleTime(5, TimeUnit.SECONDS)
            }
            .codecRegistry(CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(LongCodec())
            ))
            .build())

        database = client.getDatabase("comick-smp")
        database.listCollections().toMono().block()
    }
}