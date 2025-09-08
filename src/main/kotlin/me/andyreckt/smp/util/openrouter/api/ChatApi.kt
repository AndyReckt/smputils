package me.andyreckt.smp.util.openrouter.api

import com.google.gson.Gson
import me.andyreckt.smp.util.openrouter.config.ClientConfig
import me.andyreckt.smp.util.openrouter.models.*
import me.andyreckt.smp.util.openrouter.utils.MediaUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ChatApi(
    private val httpClient: OkHttpClient,
    private val gson: Gson,
    private val config: ClientConfig
) : BaseApi() {

     fun createCompletion(request: ChatCompletionRequest): ChatCompletionResponse {
        val requestBody = gson.toJson(request)
            .toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("${BASE_URL}/chat/completions")
            .post(requestBody)
            .build()

        return httpClient.newCall(httpRequest).execute().use { response ->
            handleResponse<ChatCompletionResponse>(response, gson)
        }
    }

    // Helper methods for creating messages
    fun createTextMessage(role: String, text: String): Message {
        return Message(role = role, content = text)
    }

    fun createMultimodalMessage(
        role: String,
        text: String,
        imageBytes: ByteArray? = null,
        imageMimeType: String = "image/jpeg",
        pdfBytes: ByteArray? = null
    ): Message {
        val content = mutableListOf<ContentItem>()

        content.add(ContentItem(type = "text", text = text))

        imageBytes?.let {
            content.add(ContentItem(
                type = "image_url",
                imageUrl = ImageUrl(url = MediaUtils.encodeImageToBase64(it, imageMimeType))
            ))
        }

        pdfBytes?.let {
            content.add(ContentItem(
                type = "file",
                fileUrl = FileUrl(url = MediaUtils.encodePdfToBase64(it))
            ))
        }

        return Message(role = role, content = content)
    }

    companion object {
        const val BASE_URL = "https://openrouter.ai/api/v1"
    }
}