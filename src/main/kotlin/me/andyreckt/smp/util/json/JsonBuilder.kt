package me.andyreckt.smp.util.json

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

class JsonBuilder {
    private val jsonObject = JsonObject()

    fun add(property: String, value: String?): JsonBuilder {
        jsonObject.addProperty(property, value)
        return this
    }

    fun add(property: String, value: Number?): JsonBuilder {
        jsonObject.addProperty(property, value)
        return this
    }

    fun add(property: String, value: Boolean?): JsonBuilder {
        jsonObject.addProperty(property, value)
        return this
    }

    fun add(property: String, value: Char?): JsonBuilder {
        jsonObject.addProperty(property, value)
        return this
    }

    fun add(property: String, data: UUID): JsonBuilder {
        jsonObject.addProperty(property, data.toString())
        return this
    }

    fun add(property: String, value: JsonElement?): JsonBuilder {
        jsonObject.add(property, value)
        return this
    }

    fun build(): JsonObject {
        return this.jsonObject
    }

    fun buildString(): String {
        return jsonObject.toString()
    }
}
