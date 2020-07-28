package com.example.nexmoverify.textbelt

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextBeltCheckResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "isValidOtp") val isValidOtp: Boolean
) {
    override fun toString(): String {
        return "TextBeltCheckResponse: success=$success, isValidOtp=$isValidOtp"
    }
}