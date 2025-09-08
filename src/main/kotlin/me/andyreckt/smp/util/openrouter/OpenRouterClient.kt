package me.andyreckt.smp.util.openrouter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.andyreckt.smp.util.openrouter.api.*
import me.andyreckt.smp.util.openrouter.config.ClientConfig
import me.andyreckt.smp.util.openrouter.constants.Models
import me.andyreckt.smp.util.openrouter.interceptors.AuthInterceptor
import me.andyreckt.smp.util.openrouter.interceptors.RetryInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OpenRouterClient private constructor(
    private val apiKey: String,
    private val httpClient: OkHttpClient,
    internal val gson: Gson,
    internal val config: ClientConfig
) {
    companion object {
        const val BASE_URL = "https://openrouter.ai/api/v1"

        fun create(
            apiKey: String,
            appUrl: String? = null,
            appName: String? = null,
            enableRetries: Boolean = true,
            enableLogging: Boolean = false,
            config: ClientConfig = ClientConfig()
        ): OpenRouterClient {
            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(apiKey, appUrl, appName))

            if (enableRetries) {
                clientBuilder.addInterceptor(RetryInterceptor(config))
            }

            if (enableLogging) {
                clientBuilder.addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                )
            }

            val gson = GsonBuilder()
                .setFieldNamingPolicy(com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

            return OpenRouterClient(apiKey, clientBuilder.build(), gson, config)
        }
    }

    val chat = ChatApi(httpClient, gson, config)
    val models = ModelsApi(httpClient, gson, config)
    val auth = AuthApi(httpClient, gson, config)
    val generations = GenerationsApi(httpClient, gson, config)
    val crypto = CryptoApi(httpClient, gson, config)

    fun setDefaultModel(string: String) {
        Models.DEFAULT = string
    }
}
