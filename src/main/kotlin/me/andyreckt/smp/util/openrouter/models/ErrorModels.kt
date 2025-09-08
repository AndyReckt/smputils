package me.andyreckt.smp.util.openrouter.models

data class ApiError(
    val error: ErrorDetails
)

data class ErrorDetails(
    val code: Int,
    val message: String,
    val type: String? = null,
    val metadata: Map<String, String>? = null
)

class OpenRouterException(
    val errorCode: Int,
    val errorType: String?,
    override val message: String,
    val metadata: Map<String, String>? = null
) : Exception(message)
