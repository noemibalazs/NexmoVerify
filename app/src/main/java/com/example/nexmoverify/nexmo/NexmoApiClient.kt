package com.example.nexmoverify.nexmo

import com.example.nexmoverify.util.NEXMO_BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NexmoApiClient {

    @GET("json")
    fun getVerificationCode(
        @Query("api_key") api_key: String,
        @Query("api_secret") api_secret: String,
        @Query("number") number: String,
        @Query("brand") brand: String
    ): Call<NexmoVerifyResponse>

    @GET("check/json")
    fun checkVerificationCode(
        @Query("api_key") api_key: String,
        @Query("api_secret") api_secret: String,
        @Query("request_id") request_id: String,
        @Query("code") code: Int
    ): Call<NexmoCheckResponse>
    companion object {
        private val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
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