package com.example.nexmoverify.region

import android.content.Context
import android.telephony.TelephonyManager
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nexmoverify.helper.DataManager
import com.example.nexmoverify.helper.SingleLiveData
import com.example.nexmoverify.nexmo.NexmoApiClient
import com.example.nexmoverify.nexmo.NexmoVerifyBody
import com.example.nexmoverify.nexmo.NexmoVerifyResponse
import com.example.nexmoverify.otp.AppSignatureHelper
import com.example.nexmoverify.util.NEXMO_API_KEY
import com.example.nexmoverify.util.NEXMO_BRAND
import com.example.nexmoverify.util.NEXMO_SECRET_KEY
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import org.koin.core.logger.KOIN_TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RegionViewModel(
    private val appSignatureHelper: AppSignatureHelper,
    private val regionManager: RegionManager,
    private val dataManager: DataManager,
    private val context: Context,
    private val nexmoApiClient: NexmoApiClient
) : ViewModel() {

    val mutableRegionList = MutableLiveData<List<Region>>()
    val onCountryCodeClicked = SingleLiveData<Any>()
    val onVerifyPhoneNumberClicked = SingleLiveData<Any>()

    val mutablePhoneNumber = ObservableField<String>()
    val mutablePrefix = ObservableField<String>()
    val mutableFailedNumber = SingleLiveData<Boolean>()

    init {
        loadRegions()
    }

    private fun loadRegions() {
        Logger.d(KOIN_TAG, "loadRegions")
        CoroutineScope(Dispatchers.IO).launch {
            val result = regionManager.loadRegions()
            withContext(Dispatchers.Main) {
                try {
                    mutableRegionList.value = mappingRegions(result)
                    if (!isActive) {
                        return@withContext
                    }
                } catch (e: Exception) {
                    Logger.e(KOIN_TAG, "loadRegions exception error: ${e.message}")
                }
            }
        }
    }

    private fun mappingRegions(list: List<Region>): List<Region> {
        val countryCode = dataManager.getRegionCountryCode()
        return list.map { region ->
            Region(
                countryName = region.countryName,
                countryCode = region.countryCode,
                locale = region.locale,
                matches = countryCode == region.countryCode
            )
        }
    }

    fun onRegionCodeClicked() {
        Logger.d(KOIN_TAG, "onRegionCodeClicked")
        onCountryCodeClicked.call()
    }

    fun onVerifyPhoneNumberClicked() {
        Logger.d(KOIN_TAG, "onVerifyPhoneNumberClicked")
        if (isValidPhoneNumber()) {
            onVerifyPhoneNumberClicked.call()
        } else
            mutableFailedNumber.value = true
    }

    private fun isValidPhoneNumber(): Boolean {
        val phoneNumber = mutablePhoneNumber.get()
        if (phoneNumber.isNullOrEmpty())
            return false

        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        try {

            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val country = telephonyManager.simCountryIso.toUpperCase()
            val number = phoneNumberUtil.parse(phoneNumber, Locale("", country).country)
            val numberType = phoneNumberUtil.getNumberType(number)
            return numberType == PhoneNumberUtil.PhoneNumberType.MOBILE

        } catch (e: Exception) {
            Logger.e(KOIN_TAG, "isValidPhoneNumber throw error: ${e.message}")
        }
        return false
    }

    private fun getVerificationCode() {
        CoroutineScope(Dispatchers.IO).launch {
            val number = "${mutablePrefix.get()!!}${mutablePhoneNumber.get()!!}"
            val verifyBody = NexmoVerifyBody(
                api_key = NEXMO_API_KEY,
                api_secret = NEXMO_SECRET_KEY,
                brand = NEXMO_BRAND,
                number = number
            )
            val result = nexmoApiClient.verificationCode(verifyBody)
            withContext(Dispatchers.Main) {
                try {
                    result.enqueue(object : Callback<NexmoVerifyResponse> {
                        override fun onFailure(call: Call<NexmoVerifyResponse>, t: Throwable) {
                            Logger.e(KOIN_TAG, "onFailure error: ${t.message}")
                        }

                        override fun onResponse(
                            call: Call<NexmoVerifyResponse>,
                            response: Response<NexmoVerifyResponse>
                        ) {
                            if (!response.isSuccessful) {
                                Logger.d(KOIN_TAG, "onResponse failure: ${response.errorBody()}")
                            }

                            if (response.isSuccessful) {
                                Logger.d(KOIN_TAG, "onResponse is success, launch check code")
                                val nexmoVerifyResponse = response.body()
                                nexmoVerifyResponse?.let {
                                    dataManager.saveNexmoRequestId(it.request_id)
                                }
                            }
                        }

                    })

                    if (!isActive) {
                        return@withContext
                    }

                } catch (e: Exception) {
                    Logger.e(KOIN_TAG, "getVerificationCode error: ${e.message}")
                }
            }
        }
    }

    fun setCountryPrefix(prefix: String) {
        mutablePrefix.set(prefix)
    }
}