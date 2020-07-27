package com.example.nexmoverify.nexmo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NexmoCheckResponse(
    @Json(name = "request_id") val request_id: String,
    @Json(name = "status") val status: String,
    @Json(name = "event_id") val event_id: String,
    @Json(name = "price") val price: String,
    @Json(name = "currency") val currency: String
) {
    override fun toString(): String {
        return "NexmoCheckResponse: request_id='$request_id', status='$status', event_id='$event_id', price='$price', currency='$currency'"
    }

}