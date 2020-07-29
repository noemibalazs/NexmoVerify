package com.example.nexmoverify.checkcode

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.nexmoverify.helper.DataManager
import com.example.nexmoverify.helper.SingleLiveData
import com.example.nexmoverify.otp.AppSignatureHelper
import com.example.nexmoverify.textbelt.TextBeltApiClient
import com.example.nexmoverify.textbelt.TextBeltCheckResponse
import com.example.nexmoverify.util.TEXT_BELT_KEY
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import org.koin.core.logger.KOIN_TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckCodeViewModel(
    private val appSignatureHelper: AppSignatureHelper,
    private val dataManager: DataManager,
    private val textBeltApiClient: TextBeltApiClient
) : ViewModel() {

    init {
        checkGeneratedCode()
    }

    val mutableIsValidOTP = SingleLiveData<Boolean>()

    val mutableFirstDigit = ObservableField<String>()
    val mutableSecondDigit = ObservableField<String>()
    val mutableThirdDigit = ObservableField<String>()
    val mutableForthDigit = ObservableField<String>()
    val mutableFifthDigit = ObservableField<String>()
    val mutableSixthDigit = ObservableField<String>()

    val allDigitsAreIntroduced = SingleLiveData<Boolean>()

    private val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            sender?.let {

            }
        }
    }

    private fun checkGeneratedCode() {
        CoroutineScope(Dispatchers.IO).launch {
            val userID = appSignatureHelper.getAppSignature()[0]
            val checkResult = textBeltApiClient.checkOTP(
                key = TEXT_BELT_KEY,
                userid = userID, otp = dataManager.getOTP()
            )
            withContext(Dispatchers.Main) {
                try {
                    checkResult.enqueue(object : Callback<TextBeltCheckResponse> {
                        override fun onFailure(call: Call<TextBeltCheckResponse>, t: Throwable) {
                            Logger.e(KOIN_TAG, "onFailure error message: ${t.message}")
                        }

                        override fun onResponse(
                            call: Call<TextBeltCheckResponse>,
                            response: Response<TextBeltCheckResponse>
                        ) {
                            if (!response.isSuccessful) {
                                Logger.e(KOIN_TAG, "onResponse failure code: ${response.code()}")
                            }

                            if (response.isSuccessful) {
                                Logger.e(KOIN_TAG, "onResponse failure successful!")
                                val checkResponse = response.body()
                                checkResponse?.let {
                                    mutableIsValidOTP.value = it.isValidOtp
                                }
                            }
                        }
                    })

                    if (!isActive)
                        return@withContext

                } catch (e: Exception) {
                    Logger.e(KOIN_TAG, "checkGeneratedCode exception error: ${e.message}")
                }
            }
        }
    }

    fun onContinueClicked() {
        Logger.d(KOIN_TAG, "onContinueClicked")
    }
}