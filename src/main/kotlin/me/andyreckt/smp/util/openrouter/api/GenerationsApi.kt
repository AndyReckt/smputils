package me.andyreckt.smp.util.openrouter.api

import com.google.gson.Gson
import me.andyreckt.smp.util.openrouter.config.ClientConfig
import me.andyreckt.smp.util.openrouter.models.GenerationDetails
import okhttp3.OkHttpClient
import okhttp3.Request

class GenerationsApi(
    private val httpClient: OkHttpClient,
    private val gson: Gson,
    private val config: ClientConfig
) : BaseApi() {

     fun getGeneration(generationId: String): GenerationDetails {
        val request = Request.Builder()
            .url("${BASE_URL}/generation/$generationId")
            .get()
            .build()

        return httpClient.newCall(request).execute().use { response ->
            handleResponse<GenerationDetails>(response, gson)
        }
    }

    companion object {
        const val BASE_URL = "https://openrouter.ai/api/v1"
    }
}