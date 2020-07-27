package com.example.nexmoverify.nexmo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NexmoVerifyResponse(
    @Json(name = "request_id") val request_id: String,
    @Json(name = "status") val status: String
) {
    override fun toString(): String {
        return "OTPResponse: request_id='$request_id', status='$status'"
    }
}
