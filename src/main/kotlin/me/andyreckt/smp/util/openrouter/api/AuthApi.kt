package me.andyreckt.smp.util.openrouter.api

import com.google.gson.Gson
import me.andyreckt.smp.util.openrouter.config.ClientConfig
import me.andyreckt.smp.util.openrouter.models.AuthStatusResponse
import me.andyreckt.smp.util.openrouter.models.RateLimitInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class AuthApi(
    private val httpClient: OkHttpClient,
    private val gson: Gson,
    private val config: ClientConfig
) : BaseApi() {

     fun getAuthStatus(): AuthStatusResponse {
        val request = Request.Builder()
            .url("${BASE_URL}/auth/key")
            .get()
            .build()

        return httpClient.newCall(request).execute().use { response ->
            handleResponse<AuthStatusResponse>(response, gson)
        }
    }

    fun extractRateLimitInfo(response: Response): RateLimitInfo? {
        val limit = response.header("X-RateLimit-Limit")?.toIntOrNull()
        val remaining = response.header("X-RateLimit-Remaining")?.toIntOrNull()
        val reset = response.header("X-RateLimit-Reset")?.toLongOrNull()

        return if (limit != null && remaining != null && reset != null) {
            RateLimitInfo(limit, remaining, reset * 1000) // Convert to milliseconds
        } else null
    }

    companion object {
        const val BASE_URL = "https://openrouter.ai/api/v1"
    }
}
