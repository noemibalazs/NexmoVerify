package com.example.nexmoverify.otp

interface OTPListener {

    fun onOTPReceived(otp: String)
    fun onOTPTimeOut()
    fun onOTPError(error: String)
}