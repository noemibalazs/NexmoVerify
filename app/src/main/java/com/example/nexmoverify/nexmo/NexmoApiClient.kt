package com.example.nexmoverify.nexmo

import com.example.nexmoverify.util.NEXMO_BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET

interface NexmoApiClient {

    @GET("json")
    fun verificationCode(@Body body: NexmoVerifyBody): Call<NexmoVerifyResponse>

    @GET("check/json")
    fun checkVerificationCode(@Body body: NexmoCheckBody): Call<NexmoCheckResponse>

    companion object {
        val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        fun getNexmoApiClient(): NexmoApiClient {
            return Retrofit.Builder()
                .baseUrl(NEXMO_BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .build().create(NexmoApiClient::class.java)
        }
    }
}