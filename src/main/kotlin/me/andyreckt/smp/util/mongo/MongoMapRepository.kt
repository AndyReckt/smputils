package me.andyreckt.smp.util.mongo

import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoCollection
import lombok.Getter
import org.bson.Document
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Getter
abstract class MongoMapRepository<K, V> protected constructor(
    /** The type of the object  */
    private val type: Type, collection: MongoCollection<Document>
) {
    /** The repository  */
    protected val repository = MongoRepository<V>(type, collection)

    /** The cache  */
    protected val cache: MutableMap<K, V> = ConcurrentHashMap()

    /**
     * Save to the database
     * @param key The key
     * @param value The value
     */
    fun saveToDatabase(key: K, value: V) {
        repository.replace(Filters.eq("_id", key.toString()), value).subscribe()
    }

    protected fun saveToDatabaseBlocking(key: K, value: V) {
        repository.replace(Filters.eq("_id", key.toString()), value).block()
    }

    protected fun saveToDatabaseBlocking(map: Map<K, V>) {
        map.forEach { (key, value) -> saveToDatabaseBlocking(key, value) }
    }

    @get:Deprecated("Use {@link #getAllEntriesFromDatabase()} instead")
    val allEntriesFromDatabaseFuture: CompletableFuture<List<V>>
        /**
         * Get all entries from the database
         * @return The list of entries
         */
        get() = repository.findAll(type).collectList().toFuture()

    val allEntriesFromDatabase: Flux<V>
        /**
         * Get all entries from the database using the reactive streams api
         * @return The list of entries
         */
        get() = repository.findAll(type)

    /**
     * Get from the database
     * @param key The key
     * @return The value
     */
    fun getFromDatabaseSync(key: K): V? {
        return repository.findOne(type, Filters.eq("_id", key.toString())).block()
    }

    /**
     * Get from the database
     * @param key The key
     * @return The value
     */
    @Deprecated("Use {@link #getFromDatabase(K)} instead")
    fun getFromDatabaseFuture(key: K): CompletableFuture<V> {
        return repository.findOne(type, Filters.eq("_id", key.toString())).toFuture()
    }

    /**
     * Get from the database using the reactive streams api
     * @param key The key
     * @return The value
     */
    fun getFromDatabase(key: K): Mono<V> {
        return repository.findOne(type, Filters.eq("_id", key.toString()))
    }

    /**
     * Add to the cache
     * @param key The key
     * @param value The value
     */
    open fun addToCache(key: K, value: V) {
        cache[key] = value
    }

    /**
     * Remove from the cache
     * @param key The key
     */
    open fun removeFromCache(key: K) {
        cache.remove(key)
    }

    /**
     * Get from the cache
     * @param key The key
     */
    open fun getFromCache(key: K): V? {
        return cache[key]
    }

    /**
     * Remove from the database
     * @param key The key
     */
    fun removeFromDatabase(key: K) {
        repository.delete(Filters.eq("_id", key.toString())).subscribe()
    }
}