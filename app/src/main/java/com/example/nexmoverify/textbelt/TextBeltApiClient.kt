package com.example.nexmoverify.textbelt

import com.example.nexmoverify.util.TEXT_BELT_BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface TextBeltApiClient {

    @FormUrlEncoded
    @POST("generate")
    fun generateOTP(@Body body: TextBeltVerifyBody): Call<TextBeltVerifyResponse>

    @GET("verify")
    fun checkOTP(
        @Query("key") key: String,
        @Query("userid") userid: String,
        @Query("otp") otp: String
    ): Call<TextBeltCheckResponse>

    companion object {

        private val interceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        fun getTextBeltApiClient(): TextBeltApiClient {
            return Retrofit.Builder().baseUrl(TEXT_BELT_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(client)
                .build().create(TextBeltApiClient::class.java)
        }
    }
}