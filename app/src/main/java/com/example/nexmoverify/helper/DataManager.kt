package com.example.nexmoverify.helper

import android.content.Context
import com.example.nexmoverify.util.*

class DataManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)

    fun saveRegionCountryCode(countryCode: Int) {
        sharedPreferences.edit().putInt(REGION_COUNTRY_CODE_KEY, countryCode).apply()
    }

    fun getRegionCountryCode(): Int {
        return sharedPreferences.getInt(REGION_COUNTRY_CODE_KEY, 0)
    }

    fun saveRegionCountry(country: String) {
        sharedPreferences.edit().putString(REGION_COUNTRY_KEY, country).apply()
    }

    fun getRegionCountry(): String {
        return sharedPreferences.getString(REGION_COUNTRY_KEY, "") ?: ""
    }

    fun saveNexmoRequestId(requestId: String) {
        sharedPreferences.edit().putString(NEXMO_REGUEST_ID_KEY, requestId).apply()
    }

    fun getNexmoRequestID(): String {
        return sharedPreferences.getString(NEXMO_REGUEST_ID_KEY, "") ?: ""
    }

    fun saveOTP(otp: String) {
        sharedPreferences.edit().putString(TEXT_BELT_OTP_KEY, otp).apply()
    }

    fun getOTP(): String {
        return sharedPreferences.getString(TEXT_BELT_OTP_KEY, "") ?: ""
    }

    fun saveUserPhoneNumber(number: String) {
        sharedPreferences.edit().putString(USER_PHONE_NUMBER_KEY, number).apply()
    }

    fun getUserPhoneNumber(): String {
        return sharedPreferences.getString(USER_PHONE_NUMBER_KEY, "") ?: ""
    }
}