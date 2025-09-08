package me.andyreckt.smp.util.openrouter.interceptors

import me.andyreckt.smp.util.openrouter.config.ClientConfig
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.math.min
import kotlin.random.Random

class RetryInterceptor(private val config: ClientConfig) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        var attempt = 0

        while (shouldRetry(response.code) && attempt < config.maxRetries) {
            response.close()
            attempt++

            val delay = calculateBackoff(attempt)
            Thread.sleep(delay)

            response = chain.proceed(chain.request())
        }

        return response
    }

    private fun shouldRetry(code: Int): Boolean = when (code) {
        408, 429, 502, 503, 504 -> true // Timeout, rate limit, server errors
        else -> false
    }

    private fun calculateBackoff(attempt: Int): Long {
        val exponentialDelay = config.baseRetryDelayMs * (1 shl attempt)
        val jitter = Random.nextDouble(0.5, 1.5)
        return min(exponentialDelay * jitter, config.maxRetryDelayMs.toDouble()).toLong()
    }
}