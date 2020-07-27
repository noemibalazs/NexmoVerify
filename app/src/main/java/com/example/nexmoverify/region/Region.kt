package com.example.nexmoverify.region

import android.os.Parcelable
import com.example.nexmoverify.helper.UserCountryCodeListener
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Region(
    val countryName: String,
    val countryCode: Int,
    val locale: Locale,
    override var matches: Boolean = false
) : UserCountryCodeListener, Parcelable {
    override fun toString(): String {
        return "$countryName  +$countryCode"
    }
}