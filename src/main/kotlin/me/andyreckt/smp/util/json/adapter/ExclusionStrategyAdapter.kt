package me.andyreckt.smp.util.json.adapter

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import me.andyreckt.smp.util.json.annotation.Exclude

class ExclusionStrategyAdapter : ExclusionStrategy {
    override fun shouldSkipField(field: FieldAttributes): Boolean {
        return field.getAnnotation(Exclude::class.java) != null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return clazz.getAnnotation(Exclude::class.java) != null
    }
}
