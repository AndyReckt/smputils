package me.andyreckt.smp.util.openrouter.models

import com.google.gson.annotations.SerializedName

data class AuthStatusResponse(
    val data: AuthData
)

data class AuthData(
    val label: String,
    val usage: Double,
    val limit: Double? = null,
    @SerializedName("is_free_tier") val isFreeTier: Boolean,
    @SerializedName("rate_limit") val rateLimit: RateLimitData
)

data class RateLimitData(
    val requests: Int,
    val interval: String
)

data class RateLimitInfo(
    val limit: Int,
    val remaining: Int,
    val resetTime: Long // Unix timestamp in milliseconds
)