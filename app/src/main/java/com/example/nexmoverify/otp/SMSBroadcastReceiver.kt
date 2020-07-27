package com.example.nexmoverify.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSBroadcastReceiver : BroadcastReceiver() {

    private var otpListener: OTPListener? = null

    fun setOTPListener(listener: OTPListener) {
        otpListener = listener
    }

    override fun onReceive(context: Context?, data: Intent?) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION == data?.action) {
            val extras = data.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    otpListener?.onOTPReceived(message)
                }

                CommonStatusCodes.TIMEOUT -> {
                    otpListener?.onOTPTimeOut()
                }

                CommonStatusCodes.ERROR -> {
                    otpListener?.onOTPError("Something went wrong, please try it again!")
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    otpListener?.onOTPError("Network error!")
                }

                CommonStatusCodes.API_NOT_CONNECTED -> {
                    otpListener?.onOTPError("API not connected!")
                }
            }
        }
    }
}