# OpenRouter Kotlin Client

A comprehensive Kotlin wrapper for the OpenRouter API with support for 300+ AI models, multimodal inputs, streaming, and advanced routing.

## Features

- **Full OpenRouter API Support**: Chat completions, model management, authentication
- **Multimodal Support**: Images (JPEG, PNG, GIF, WebP) and PDF processing
- **Streaming**: Real-time response streaming with coroutines
- **Model Routing**: Automatic fallbacks, provider selection, cost optimization
- **Error Handling**: Comprehensive error handling with retry strategies
- **Type Safety**: Fully typed Kotlin models with Gson serialization

## Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

## Quick Start

```kotlin
import com.openrouter.client.OpenRouterClient
import com.openrouter.client.constants.Models
import com.openrouter.client.models.ChatCompletionRequest

// Initialize client
val client = OpenRouterClient.create(
    apiKey = "your-openrouter-api-key",
    appName = "My Kotlin App"
)

// Basic chat completion
val request = ChatCompletionRequest(
    model = Models.GPT_4O_MINI,
    messages = listOf(
        client.chat.createTextMessage("user", "Hello, how are you?")
    ),
    maxTokens = 100
)

val response = client.chat.createCompletion(request)
println(response.choices.first().message.content)
```

## Advanced Usage

### Streaming Responses

```kotlin
client.chat.createCompletionStream(request).collect { chunk ->
    chunk.choices.forEach { choice ->
        choice.delta.content?.let { content ->
            print(content)
        }
    }
}
```

### Image Analysis

```kotlin
val imageBytes = File("image.jpg").readBytes()

val request = ChatCompletionRequest(
    model = Models.GPT_4O,
    messages = listOf(
        client.chat.createMultimodalMessage(
            role = "user",
            text = "What do you see in this image?",
            imageBytes = imageBytes,
            imageMimeType = "image/jpeg"
        )
    )
)
```

### PDF Processing

```kotlin
val pdfBytes = File("document.pdf").readBytes()

val request = ChatCompletionRequest(
    model = Models.GPT_4O,
    messages = listOf(
        client.chat.createMultimodalMessage(
            role = "user",
            text = "Summarize this document",
            pdfBytes = pdfBytes
        )
    ),
    plugins = listOf(
        PluginConfig(
            name = "file",
            config = mapOf("engine" to "mistral-ocr")
        )
    )
)
```

### Model Fallbacks and Routing

```kotlin
val request = ChatCompletionRequest(
    model = Models.GPT_4O,
    models = listOf(
        Models.GPT_4O,
        Models.CLAUDE_3_5_SONNET,
        Models.GEMINI_PRO_1_5
    ),
    provider = ProviderConfig(
        allowFallbacks = true,
        dataCollection = "deny",
        sort = "price"
    )
)
```

## Available Models

The client includes constants for popular models:

- `Models.AUTO` - Intelligent auto-routing
- `Models.GPT_4O` - OpenAI GPT-4o
- `Models.CLAUDE_3_5_SONNET` - Anthropic Claude 3.5 Sonnet
- `Models.GEMINI_PRO_1_5` - Google Gemini Pro 1.5
- `Models.LLAMA_3_1_405B` - Meta Llama 3.1 405B
- `Models.GPT_4O_NITRO` - High throughput variant
- `Models.CLAUDE_3_5_SONNET_FLOOR` - Lowest cost variant

## Configuration

```kotlin
val config = ClientConfig(
    connectTimeoutSeconds = 30L,
    readTimeoutSeconds = 300L,
    streamReadTimeoutSeconds = 600L,
    maxRetries = 3,
    baseRetryDelayMs = 1000L,
    maxRetryDelayMs = 60000L
)

val client = OpenRouterClient.create(
    apiKey = "your-api-key",
    appName = "My App",
    enableRetries = true,
    enableLogging = true,
    config = config
)
```

## Error Handling

```kotlin
try {
    val response = client.chat.createCompletion(request)
} catch (e: OpenRouterException) {
    println("Error ${e.errorCode}: ${e.message}")
    e.metadata?.let { metadata ->
        println("Additional info: $metadata")
    }
}
```

## Authentication and Usage Monitoring

```kotlin
// Check account status
val authStatus = client.auth.getAuthStatus()
println("Usage: ${authStatus.data.usage}")
println("Limit: ${authStatus.data.limit}")

// Get generation details
val generationDetails = client.generations.getGeneration("gen_id")
println("Tokens used: ${generationDetails.tokensPrompt + generationDetails.tokensCompletion}")
```
