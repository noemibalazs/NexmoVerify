package com.example.nexmoverify.region

import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*

class RegionManager(
    private val phoneNumberUtil: PhoneNumberUtil
) {

    fun loadRegions(): List<Region> {
        return phoneNumberUtil.supportedRegions.asSequence()
            .map { region -> Locale("en", region) }
            .map { locale ->
                val countryCode = phoneNumberUtil.getCountryCodeForRegion(locale.country)
                Region(
                    countryName = locale.displayCountry,
                    countryCode = countryCode,
                    locale = locale
                )
            }.sortedBy { region -> region.countryName }
            .toList()
    }
}