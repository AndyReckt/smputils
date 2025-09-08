package me.andyreckt.smp.util.json.adapter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import me.andyreckt.smp.util.json.`interface`.PostProcessable

class PostProcessAdapter : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, type)

        return object : TypeAdapter<T>() {
            override fun write(out: com.google.gson.stream.JsonWriter?, value: T) {
                delegate.write(out, value)
            }

            override fun read(`in`: com.google.gson.stream.JsonReader?): T {
                val obj = delegate.read(`in`)

                if (obj is PostProcessable) {
                    obj.postProcess()
                }

                return obj
            }
        }
    }
}