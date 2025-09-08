package me.andyreckt.smp.util.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoCollection
import me.andyreckt.smp.util.other.Statics
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.json.JsonWriterSettings
import org.bson.json.StrictJsonWriter
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.reflect.Type
import java.util.*

/**
 * Eases the use of a MongoDB collection using the reactive streams api.
 */
open class MongoRepository<T>(
    /** The type of the documents in the collection.  */
    protected val type: Type,
    /** The collection to use.  */
    protected var collection: MongoCollection<Document>
) {
    /**
     * Creates a new MongoRepository.
     *
     * @param type      The type of the documents in the collection.
     * @param collection The collection to use.
     */
    init {
        collection.listIndexes()
    }

    /**
     * Deletes a document from the collection.
     *
     * @param query The query to filter the document.
     * @return The deleted document.
     */
    fun delete(query: Bson): Mono<Document> {
        return Mono.from(collection.findOneAndDelete(query))
    }

    /**
     * Finds a document in the collection and maps it to the given class.
     *
     * @param type The type to map the document to.
     * @param query The query to filter the document.
     * @return The mapped document.
     */
    fun findOne(type: Type, query: Bson): Mono<T> {
        return findOne(type, query, null)
    }

    /**
     * Finds a document in the collection and maps it to the given class.
     *
     * @param type The type to map the document to.
     * @param query The query to filter the document.
     * @param sort The sort to apply to the query.
     * @return The mapped document.
     */
    fun findOne(type: Type, query: Bson, sort: Bson?): Mono<T> {
        var req = collection.find(query)
        if (sort != null) {
            req = req.sort(sort)
        }
        return Mono.from(req.first()).map {
            Statics.GSON.fromJson(
                it.toJson(JSON_WRITER_SETTINGS),
                type
            )
        }
    }

    /**
     * Finds a document in the collection and maps it to the given class.
     *
     * @param clazz The class to map the document to.
     * @param query The query to filter the document.
     * @return The mapped document.
     */
    fun findOne(clazz: Class<T>, query: Bson): Mono<T> {
        return findOne(clazz, query, null)
    }

    /**
     * Finds a document in the collection and maps it to the given class.
     *
     * @param clazz The class to map the document to.
     * @param query The query to filter the document.
     * @param sort The sort to apply to the query.
     * @return The mapped document.
     */
    fun findOne(clazz: Class<T>, query: Bson, sort: Bson?): Mono<T> {
        var req = collection.find(query)
        if (sort != null) {
            req = req.sort(sort)
        }
        return Mono.from(req.first()).map {
            Statics.GSON.fromJson(
                it.toJson(JSON_WRITER_SETTINGS),
                clazz
            )
        }
    }

    /**
     * Finds a document in the collection.
     *
     * @param query The query to filter the document.
     * @return The document.
     */
    fun findOne(query: Bson): Mono<Document> {
        return Mono.from(collection.find(query).first())
    }

    /**
     * Finds all documents in the collection and maps them to the given class.
     *
     * @param clazz The class to map the documents to.
     * @return The mapped documents.
     */
    fun findAll(clazz: Class<T>): Flux<T> {
        return Flux.from(collection.find()).map {
            Statics.GSON.fromJson(
                it.toJson(JSON_WRITER_SETTINGS),
                clazz
            )
        }
    }

    /**
     * Finds all documents in the collection and maps them to the given type.
     *
     * @param type The type to map the documents to.
     * @return The mapped documents.
     */
    fun findAll(type: Type): Flux<T> {
        return Flux.from(collection.find()).map {
            Statics.GSON.fromJson(
                it.toJson(JSON_WRITER_SETTINGS),
                type
            )
        }
    }

    /**
     * Finds all documents in the collection.
     *
     * @return The documents.
     */
    fun findAll(): Flux<Document> {
        return Flux.from(collection.find())
    }

    /**
     * Finds all documents in the collection that match the given query and maps them to the given class.
     *
     * @param clazz The class to map the documents to.
     * @param query The query to filter the documents.
     * @return The mapped documents.
     */
    fun find(clazz: Class<T>, query: Bson): Flux<T> {
        return Flux.from(collection.find(query)).map {
            Statics.GSON.fromJson(
                it.toJson(JSON_WRITER_SETTINGS),
                clazz
            )
        }
    }

    /**
     * Finds all documents in the collection that match the given query and maps them to the given type.
     *
     * @param type The type to map the documents to.
     * @param query The query to filter the documents.
     * @return The mapped documents.
     */
    fun find(type: Type, query: Bson): Flux<T> {
        return Flux.from(collection.find(query)).map {
            Statics.GSON.fromJson(
                it.toJson(JSON_WRITER_SETTINGS),
                type
            )
        }
    }

    /**
     * Counts the documents in the collection that match the given query.
     *
     * @param query The query to filter the documents.
     * @return The count of the documents.
     */
    fun count(query: Bson): Mono<Long> {
        return Mono.from(collection.countDocuments(query))
    }

    /**
     * Finds all distinct ids in the collection.
     *
     * @param aClass The class of the ids.
     * @return The distinct ids.
     */
    fun findIds(aClass: Class<T>): Flux<T> {
        return Flux.from(collection.distinct("_id", aClass))
    }

    /**
     * Replaces a document in the collection.
     *
     * @param query The query to filter the document.
     * @param obj   The object to replace the document with.
     * @return The update result.
     */
    fun replace(query: Bson, obj: Any?): Mono<UpdateResult> {
        return replace(query, obj, true)
    }

    /**
     * Replaces a document in the collection.
     *
     * @param query  The query to filter the document.
     * @param obj    The object to replace the document with.
     * @param upsert Whether to upsert the document.
     * @return The update result.
     */
    fun replace(query: Bson, obj: Any?, upsert: Boolean): Mono<UpdateResult> {
        return Mono.from(
            collection.replaceOne(
                query,
                Document.parse(Statics.GSON.toJson(obj)),
                ReplaceOptions().upsert(upsert)
            )
        )
    }

    /**
     * Replaces a document in the collection.
     *
     * @param query    The query to filter the document.
     * @param document The document to replace the document with.
     * @return The update result.
     */
    fun replace(query: Bson, document: Document): Mono<UpdateResult> {
        return replace(query, document, true)
    }

    /**
     * Replaces a document in the collection.
     *
     * @param query    The query to filter the document.
     * @param document The document to replace the document with.
     * @param upsert   Whether to upsert the document.
     * @return The update result.
     */
    fun replace(query: Bson, document: Document, upsert: Boolean): Mono<UpdateResult> {
        return Mono.from(collection.replaceOne(query, document, ReplaceOptions().upsert(upsert)))
    }

    /**
     * Updates a document in the collection.
     *
     * @param query    The query to filter the document.
     * @param document The document to update the document with.
     * @param upsert   Whether to upsert the document.
     * @return The update result.
     */
    fun updateOne(query: Bson, document: Bson, upsert: Boolean): Mono<UpdateResult> {
        return Mono.from(collection.updateOne(query, document, UpdateOptions().upsert(upsert)))
    }

    /**
     * Inserts an object into the collection.
     *
     * @param obj The object to insert.
     * @return The insert result.
     */
    fun insert(obj: Any?): Mono<InsertOneResult> {
        // Convert the object to json and insert it.
        return Mono.from(collection.insertOne(Document.parse(Statics.GSON.toJson(obj))))
    }

    /**
     * Aggregates the collection and maps the results to the given class.
     *
     * @param clazz    The class to map the results to.
     * @param pipeline The pipeline to aggregate the collection with.
     * @return The mapped results.
     */
    fun aggregate(clazz: Class<T>, vararg pipeline: Bson?): Flux<T> {
        return Flux.from(collection.aggregate(listOf(*pipeline)))
            .map {
                Statics.GSON.fromJson(
                    it.toJson(JSON_WRITER_SETTINGS),
                    clazz
                )
            }
    }

    /**
     * Aggregates the collection.
     *
     * @param pipeline The pipeline to aggregate the collection with.
     * @return The results.
     */
    fun aggregate(vararg pipeline: Bson?): Flux<Document> {
        return Flux.from(collection.aggregate(listOf(*pipeline)))
    }

    companion object {
        /** The json writer settings to use.  */
        protected val JSON_WRITER_SETTINGS: JsonWriterSettings = JsonWriterSettings.builder()
            .objectIdConverter { objectId: ObjectId, strictJsonWriter: StrictJsonWriter ->
                strictJsonWriter.writeString(
                    objectId.toHexString()
                )
            }
            .int64Converter { value: Long, writer: StrictJsonWriter -> writer.writeNumber(value.toString()) }
            .build()

        /**
         * Creates a regex filter for the given key and value. (Case insensitive)
         *
         * @param key   The key to filter.
         * @param value The value to filter.
         * @return The regex filter.
         */
        fun equalsIgnoreCase(key: String, value: String): Bson {
            return Filters.regex(key, value, "i")
        }
    }
}
