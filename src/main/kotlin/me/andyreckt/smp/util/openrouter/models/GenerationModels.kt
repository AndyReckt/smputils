package me.andyreckt.smp.util.openrouter.models


import com.google.gson.annotations.SerializedName

data class GenerationDetails(
    val id: String,
    val model: String,
    val streamed: Boolean,
    @SerializedName("generation_time") val generationTime: Double,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("tokens_prompt") val tokensPrompt: Int,
    @SerializedName("tokens_completion") val tokensCompletion: Int,
    @SerializedName("native_tokens_prompt") val nativeTokensPrompt: Int? = null,
    @SerializedName("native_tokens_completion") val nativeTokensCompletion: Int? = null,
    @SerializedName("num_media") val numMedia: Int? = null,
    @SerializedName("app_id") val appId: Int? = null,
    val origin: String? = null,
    val usage: Double,
    val cancelled: Boolean? = null,
    val moderated: Boolean? = null
)