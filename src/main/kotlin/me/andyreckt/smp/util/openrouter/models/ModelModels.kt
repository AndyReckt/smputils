package me.andyreckt.smp.util.openrouter.models

import com.google.gson.annotations.SerializedName

data class ModelsResponse(
    val `object`: String,
    val data: List<ModelInfo>
)

data class ModelInfo(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerializedName("context_length") val contextLength: Int,
    val pricing: PricingInfo,
    @SerializedName("top_provider") val topProvider: ProviderInfo? = null,
    val architecture: ArchitectureInfo? = null,
    @SerializedName("per_request_limits") val perRequestLimits: RequestLimits? = null
)

data class PricingInfo(
    val prompt: String, // Price per million tokens
    val completion: String,
    val request: String? = null,
    val image: String? = null
)

data class ProviderInfo(
    @SerializedName("max_completion_tokens") val maxCompletionTokens: Int? = null,
    @SerializedName("is_moderated") val isModerated: Boolean? = null
)

data class ArchitectureInfo(
    val modality: String? = null,
    val tokenizer: String? = null,
    @SerializedName("instruct_type") val instructType: String? = null
)

data class RequestLimits(
    @SerializedName("prompt_tokens") val promptTokens: String? = null,
    @SerializedName("completion_tokens") val completionTokens: String? = null
)
