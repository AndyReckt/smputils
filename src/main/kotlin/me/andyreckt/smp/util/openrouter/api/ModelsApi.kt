package me.andyreckt.smp.util.openrouter.api

import com.google.gson.Gson
import me.andyreckt.smp.util.openrouter.config.ClientConfig
import me.andyreckt.smp.util.openrouter.models.ModelInfo
import me.andyreckt.smp.util.openrouter.models.ModelsResponse
import okhttp3.OkHttpClient
import okhttp3.Request

class ModelsApi(
    private val httpClient: OkHttpClient,
    private val gson: Gson,
    private val config: ClientConfig
) : BaseApi() {

     fun listModels(): List<ModelInfo> {
        val request = Request.Builder()
            .url("${BASE_URL}/models")
            .get()
            .build()

        val res = httpClient.newCall(request).execute()
        if (!res.isSuccessful) {
            throw Exception("Failed to fetch models: ${res.code} ${res.message}")
        }

        try {
            val responseBody = res.body?.string()
                ?: throw Exception("Empty response body")
            val modelsResponse = gson.fromJson(responseBody, ModelsResponse::class.java)
            return modelsResponse.data
        } catch (e: Exception) {
            throw Exception("Error parsing models response: ${e.message}", e)
        }

        return httpClient.newCall(request).execute().use { response ->
            val modelsResponse = handleResponse<ModelsResponse>(response, gson)
            modelsResponse.data
        }
    }

     fun getModel(modelId: String): ModelInfo {
        val request = Request.Builder()
            .url("${BASE_URL}/models/$modelId")
            .get()
            .build()

        return httpClient.newCall(request).execute().use { response ->
            handleResponse<ModelInfo>(response, gson)
        }
    }

    companion object {
        const val BASE_URL = "https://openrouter.ai/api/v1"
    }
}
