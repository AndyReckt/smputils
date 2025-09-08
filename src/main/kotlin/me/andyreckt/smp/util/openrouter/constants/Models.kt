package me.andyreckt.smp.util.openrouter.constants

object Models {
    // Auto-routing
    const val AUTO = "openrouter/auto"

    // Free Models
    const val LLAMA_4_MAVERICK = "meta-llama/llama-4-maverick:free"
    const val LLAMA_4_SCOUT = "meta-llama/llama-4-scout:free"
    const val MISTRAL_SMALL_3_1 = "mistralai/mistral-small-3.1-24b-instruct:free"

    // Paid Models
    const val GEMINI_2_5_FLASH = "google/gemini-2.5-flash"
    const val GEMINI_2_5_FLASH_PREVIEW = "google/gemini-2.5-flash-preview-05-20"
    const val GEMINI_2_5_FLASH_LITE = "google/gemini-2.5-flash-lite-preview-06-17"

    var DEFAULT = GEMINI_2_5_FLASH
}