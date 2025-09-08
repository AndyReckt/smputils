package me.andyreckt.smp.util.openrouter.api

import com.google.gson.Gson
import me.andyreckt.smp.util.openrouter.models.ApiError
import me.andyreckt.smp.util.openrouter.models.OpenRouterException
import okhttp3.Response

abstract class BaseApi {

    protected inline fun <reified T> handleResponse(response: Response, gson: Gson): T {
        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            try {
                val apiError = gson.fromJson(errorBody, ApiError::class.java)
                throw OpenRouterException(
                    errorCode = apiError.error.code,
                    errorType = apiError.error.type,
                    message = apiError.error.message,
                    metadata = apiError.error.metadata
                )
            } catch (e: Exception) {
                throw OpenRouterException(
                    errorCode = response.code,
                    errorType = "http_error",
                    message = "HTTP ${response.code}: ${response.message}"
                )
            }
        }

        val responseBody = response.body?.string()
            ?: throw OpenRouterException(
                errorCode = response.code,
                errorType = "empty_response",
                message = "Empty response body"
            )

        return gson.fromJson(responseBody, T::class.java)
    }
}
