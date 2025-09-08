package me.andyreckt.smp.util.openrouter.models

import com.google.gson.annotations.SerializedName
import me.andyreckt.smp.util.openrouter.constants.Models
import java.util.Collections.emptyMap

data class ChatCompletionRequest(
    val model: String? = null,
    val messages: List<Message>,
    val stream: Boolean = false,
    @SerializedName("max_tokens") val maxTokens: Int? = null,
    val temperature: Double? = null,
    @SerializedName("top_p") val topP: Double? = null,
    @SerializedName("frequency_penalty") val frequencyPenalty: Double? = null,
    @SerializedName("presence_penalty") val presencePenalty: Double? = null,
    val stop: Any? = null, // String or List<String>
    @SerializedName("response_format") val responseFormat: ResponseFormat? = null,
    val tools: List<Tool>? = null,
    @SerializedName("tool_choice") val toolChoice: Any? = null, // String or Object
    val models: List<String>? = null, // Fallback models
    val provider: ProviderConfig? = null,
    val user: String? = null,
    val seed: Int? = null,
    val logprobs: Boolean? = null,
    @SerializedName("top_logprobs") val topLogprobs: Int? = null,
    val plugins: List<PluginConfig>? = null,
)

data class Reasoning(
    val effort: String?, // "low", "medium", "high"
    @SerializedName("max_tokens") val maxTokens: Int?,
    val exclude: Boolean = true, // Exclude reasoning from response
    val enabled: Boolean = false
)

data class Message(
    val role: String, // "system", "user", "assistant", "tool"
    val content: Any, // String or List<ContentItem> for multimodal
    val name: String? = null,
    @SerializedName("tool_calls") val toolCalls: List<ToolCall>? = null,
    @SerializedName("tool_call_id") val toolCallId: String? = null
)

data class ContentItem(
    val type: String, // "text", "image_url", "file"
    val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrl? = null,
    @SerializedName("file_url") val fileUrl: FileUrl? = null
)

data class ImageUrl(
    val url: String,
    val detail: String? = null // "low", "high", "auto"
)

data class FileUrl(
    val url: String
)

data class ResponseFormat(
    val type: String = "json_object" // "text" or "json_object"
)

data class Tool(
    val type: String = "function",
    val function: FunctionDefinition
)

data class FunctionDefinition(
    val name: String,
    val description: String? = null,
    val parameters: Any? = null
)

data class ToolCall(
    val id: String,
    val type: String = "function",
    val function: FunctionCall
)

data class FunctionCall(
    val name: String,
    val arguments: String
)

data class ProviderConfig(
    val order: List<String>? = null,
    @SerializedName("allow_fallbacks") val allowFallbacks: Boolean = true,
    @SerializedName("require_parameters") val requireParameters: Boolean? = null,
    @SerializedName("data_collection") val dataCollection: String? = null, // "allow" or "deny"
    val sort: String? = null, // "price", "throughput", "latency"
    val only: List<String>? = null,
    val ignore: List<String>? = null
)

data class PluginConfig(
    val name: String = "file",
    val config: Map<String, Any> = emptyMap<String, Any>()
)

data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: UsageInfo? = null,
    @SerializedName("system_fingerprint") val systemFingerprint: String? = null
)

data class Choice(
    val index: Int,
    val message: Message,
    @SerializedName("finish_reason") val finishReason: String? = null,
    val logprobs: LogProbs? = null
)

data class LogProbs(
    val content: List<TokenLogProb>? = null
)

data class TokenLogProb(
    val token: String,
    val logprob: Double,
    val bytes: List<Int>? = null,
    @SerializedName("top_logprobs") val topLogprobs: List<TopLogProb>? = null
)

data class TopLogProb(
    val token: String,
    val logprob: Double,
    val bytes: List<Int>? = null
)

data class UsageInfo(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int,
    val cost: Double? = null
)

// Streaming models
data class ChatCompletionChunk(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<ChoiceDelta>
)

data class ChoiceDelta(
    val index: Int,
    val delta: MessageDelta,
    @SerializedName("finish_reason") val finishReason: String? = null
)

data class MessageDelta(
    val role: String? = null,
    val content: String? = null,
    @SerializedName("tool_calls") val toolCalls: List<ToolCallDelta>? = null
)

data class ToolCallDelta(
    val index: Int,
    val id: String? = null,
    val type: String? = null,
    val function: FunctionCallDelta? = null
)

data class FunctionCallDelta(
    val name: String? = null,
    val arguments: String? = null
)