package com.example.nexmoverify.textbelt

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextBeltVerifyResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "textId") val textId: String?,
    @Json(name = "error") val error: String?,
    @Json(name = "quotaRemaining") val quotaRemaining: Int,
    @Json(name = "otp") val otp: String
) {
    override fun toString(): String {
        return "TextBeltResponse: success=$success, textId=$textId, error=$error, quotaRemaining=$quotaRemaining, otp='$otp'"
    }
}