package com.example.nexmoverify.generatecode

import android.content.Context
import android.telephony.TelephonyManager
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nexmoverify.R
import com.example.nexmoverify.helper.DataManager
import com.example.nexmoverify.helper.SingleLiveData
import com.example.nexmoverify.otp.AppSignatureHelper
import com.example.nexmoverify.region.Region
import com.example.nexmoverify.region.RegionManager
import com.example.nexmoverify.textbelt.TextBeltApiClient
import com.example.nexmoverify.textbelt.TextBeltVerifyBody
import com.example.nexmoverify.textbelt.TextBeltVerifyResponse
import com.example.nexmoverify.util.TEXT_BELT_KEY
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import org.koin.core.logger.KOIN_TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class GenerateCodeViewModel(
    private val appSignatureHelper: AppSignatureHelper,
    private val regionManager: RegionManager,
    private val dataManager: DataManager,
    private val context: Context,
    private val textBeltApiClient: TextBeltApiClient
) : ViewModel() {

    val mutableRegionList = MutableLiveData<List<Region>>()
    val onCountryCodeClicked = SingleLiveData<Any>()
    val onVerifyPhoneNumberClicked = SingleLiveData<Any>()

    val mutablePhoneNumber = ObservableField<String>()
    private val mutablePrefix = ObservableField<String>()
    val errorVerifyNumber = SingleLiveData<Boolean>()

    val isValidPhoneNumber = ObservableField<Boolean>()
    private var isValid = false

    val generateCodeSuccessListener = SingleLiveData<Boolean>()
    val generateCodeErrorListener = SingleLiveData<Any>()

    private val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            sender?.let {
                when (sender) {
                    mutablePhoneNumber -> {
                        isValid = isValidPhoneNumber()
                        isValidPhoneNumber.set(isValid)
                    }
                }
            }
        }
    }

    init {
        loadRegions()
        mutablePhoneNumber.addOnPropertyChangedCallback(callback)
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
        if (isValidPhoneNumber.get() == true) {
            onVerifyPhoneNumberClicked.call()
            errorVerifyNumber.value = false
        } else
            errorVerifyNumber.value = true
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

    fun generateVerificationCode() {
        CoroutineScope(Dispatchers.IO).launch {
            val number = context.getString(
                R.string.txt_phone_number_format,
                mutablePrefix.get()!!,
                mutablePhoneNumber.get()!!
            )
            val textBeltVerifyBody = TextBeltVerifyBody(
                key = TEXT_BELT_KEY, userid = appSignatureHelper.getAppSignature()[0], message =
                context.getString(R.string.txt_text_belt_message), phone = number
            )
            dataManager.saveUserPhoneNumber(number)
            val result = textBeltApiClient.generateOTP(textBeltVerifyBody)
            withContext(Dispatchers.Main) {
                try {
                    result.enqueue(object : Callback<TextBeltVerifyResponse> {
                        override fun onFailure(call: Call<TextBeltVerifyResponse>, t: Throwable) {
                            Logger.e(KOIN_TAG, "onFailure error: ${t.message}")
                            generateCodeErrorListener.call()
                        }

                        override fun onResponse(
                            call: Call<TextBeltVerifyResponse>,
                            response: Response<TextBeltVerifyResponse>
                        ) {
                            if (!response.isSuccessful) {
                                Logger.d(KOIN_TAG, "onResponse failure: ${response.errorBody()}")
                                generateCodeErrorListener.call()
                            }

                            if (response.isSuccessful) {
                                Logger.d(KOIN_TAG, "onResponse is success, launch check code")
                                val textBeltVerifyResponse = response.body()
                                textBeltVerifyResponse?.let {
                                    generateCodeSuccessListener.value = it.success
                                    if (it.success && it.otp.length == 6)
                                        dataManager.saveOTP(it.otp)
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