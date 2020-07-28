package com.example.nexmoverify.textbelt

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextBeltVerifyBody(
    @Json(name = "key") val key: String,
    @Json(name = "userid") val userid: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "message") val message: String
) {
    override fun toString(): String {
        return "TextBeltBody: key='$key', userid='$userid', phone='$phone', message='$message'"
    }
}