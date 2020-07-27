package com.example.nexmoverify.nexmo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NexmoVerifyBody(
    @Json(name = "api_key") val api_key: String,
    @Json(name = "api_secret") val api_secret: String,
    @Json(name = "number") val number: String,
    @Json(name = "brand") val brand: String
) {
    override fun toString(): String {
        return "NexmoVerifyBody: api_key='$api_key', api_secret='$api_secret', number='$number', brand='$brand'"
    }

}