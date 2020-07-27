package com.example.nexmoverify.nexmo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NexmoCheckBody(
    @Json(name = "api_key") val api_key: String,
    @Json(name = "api_secret") val api_secret: String,
    @Json(name = "request_id") val request_id: String,
    @Json(name = "code") val code: Int
) {
    override fun toString(): String {
        return "NexmoCheckBody: api_key='$api_key', api_secret='$api_secret', request_id='$request_id', code=$code"
    }
}