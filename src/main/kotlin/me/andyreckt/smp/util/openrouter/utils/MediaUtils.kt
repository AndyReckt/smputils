package me.andyreckt.smp.util.openrouter.utils

import java.util.Base64

object MediaUtils {

    fun encodeImageToBase64(imageBytes: ByteArray, mimeType: String): String {
        val base64Data = Base64.getEncoder().encodeToString(imageBytes)
        return "data:$mimeType;base64,$base64Data"
    }

    fun encodePdfToBase64(pdfBytes: ByteArray): String {
        val base64Data = Base64.getEncoder().encodeToString(pdfBytes)
        return "data:application/pdf;base64,$base64Data"
    }

    fun validateImageFormat(mimeType: String): Boolean {
        return mimeType in listOf(
            "image/jpeg", "image/jpg", "image/png",
            "image/gif", "image/webp", "image/bmp"
        )
    }

    fun validatePdfFormat(mimeType: String): Boolean {
        return mimeType == "application/pdf"
    }
}
