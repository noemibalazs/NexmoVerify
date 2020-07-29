package com.example.nexmoverify.checkcode

import androidx.lifecycle.ViewModel
import com.example.nexmoverify.helper.DataManager
import com.example.nexmoverify.otp.AppSignatureHelper
import com.example.nexmoverify.textbelt.TextBeltApiClient

class CheckCodeViewModel(
    private val appSignatureHelper: AppSignatureHelper,
    private val dataManager: DataManager,
    private val textBeltApiClient: TextBeltApiClient
) : ViewModel() {
}