package me.andyreckt.smp.util.mongo

import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoCollection
import lombok.Getter
import org.bson.Document
import org.bson.conversions.Bson
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture

@Getter
abstract class MongoListRepository<K, V> protected constructor(
    /** The type of the object  */
    private val type: Type, collection: MongoCollection<Document>
) {
    /** The repository  */
    protected val repository = MongoRepository<V>(type, collection)

    /** The cache  */
    protected val cache: MutableList<V> = ArrayList()

    /**
     * Get from the cache
     * @param key The key
     * @return The value
     */
    abstract fun getFromCache(key: K): V?

    /**
     * Save to the database
     * @param value The value
     */
    abstract fun saveToDatabase(value: V)

    /**
     * Remove from the database
     * @param value The value
     */
    abstract fun removeFromDatabase(value: V)

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
     * Get all entries from the database using a filter
     * @param bson The filter
     * @return The value
     */
    @Deprecated("Use {@link #getAllEntriesFromDatabase(Bson)} instead")
    fun getAllEntriesFromDatabaseFuture(bson: Bson): CompletableFuture<List<V>> {
        return repository.find(type, bson).collectList().toFuture()
    }

    /**
     * Get all entries from the database using a filter and the reactive streams api
     * @param bson The filter
     * @return The value
     */
    fun getAllEntriesFromDatabase(bson: Bson): Flux<V> {
        return repository.find(type, bson)
    }

    /**
     * Get a single from the database using a filter and a sort
     * @param bson The filter
     * @return The value
     */
    @Deprecated("Use {@link #getEntryFromDatabase(Bson, Bson)} instead")
    fun getEntryFromDatabaseFuture(bson: Bson, sort: Bson? = null): CompletableFuture<V> {
        return repository.findOne(type, bson, sort).toFuture()
    }

    /**
     * Get a single from the database using a filter, a sort and the reactive streams api
     * @param bson The filter
     * @return The value
     */
    fun getEntryFromDatabase(bson: Bson, sort: Bson? = null): Mono<V> {
        return repository.findOne(type, bson, sort)
    }

    /**
     * Get a single entry from the database using a filter
     * @param bson The filter
     * @return The value
     */
    fun getEntryFromDatabaseSync(bson: Bson, sort: Bson? = null): V? {
        return repository.findOne(type, bson, sort).block()
    }

    val allEntriesSyncFromDatabase: List<V>?
        /**
         * Get all entries from the database using a filter
         * @return The value
         */
        get() = repository.findAll(type).collectList().block()

    /**
     * Get all entries from the database using a filter
     * @param bson The filter
     * @return The value
     */
    fun getAllEntriesSyncFromDatabase(bson: Bson): List<V>? {
        return repository.find(type, bson).collectList().block()
    }

    /**
     * Add to the cache
     * @param value The value
     */
    fun addToCache(value: V) {
        cache.add(value)
    }

    /**
     * Remove from the cache
     * @param value The value
     */
    fun removeFromCache(value: V) {
        cache.remove(value)
    }

    /**
     * Remove from the cache
     * @param key The key
     */
    fun removeByKey(key: K) {
        val value: V = this.getFromCache(key) ?: return

        removeFromCache(value)
    }
}