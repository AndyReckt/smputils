package me.andyreckt.smp.util.openrouter.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val apiKey: String,
    private val appUrl: String? = null,
    private val appName: String? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .apply {
                appUrl?.let { addHeader("HTTP-Referer", it) }
                appName?.let { addHeader("X-Title", it) }
            }
            .build()
        return chain.proceed(request)
    }
}