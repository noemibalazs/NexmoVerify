package com.example.nexmoverify.generatecode

import android.content.Context
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.nexmoverify.R
import com.example.nexmoverify.databinding.ActivityGenerateCodeBinding
import com.example.nexmoverify.helper.DataManager
import com.example.nexmoverify.helper.OnItemClickListener
import com.example.nexmoverify.helper.RecyclerItemClickListener
import com.example.nexmoverify.otp.AppSignatureHelper
import com.example.nexmoverify.otp.OTPListener
import com.example.nexmoverify.otp.SMSBroadcastReceiver
import com.example.nexmoverify.region.Region
import com.example.nexmoverify.region.RegionAdapter
import com.example.nexmoverify.checkcode.CheckCodeActivity
import com.example.nexmoverify.util.openActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.orhanobut.logger.Logger
import org.koin.android.ext.android.inject

class GenerateCodeActivity : AppCompatActivity(), OTPListener {

    private lateinit var smsReceiver: SMSBroadcastReceiver
    private lateinit var binding: ActivityGenerateCodeBinding
    private lateinit var regionAdapter: RegionAdapter

    private val appSignatureHelper: AppSignatureHelper by inject()
    private val generateCodeViewModel: GenerateCodeViewModel by inject()
    private val dataManager: DataManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_code)

        initListener()
        initPrefix()
        initBinding()
        initObservers()
    }

    private fun initListener() {
        smsReceiver = SMSBroadcastReceiver()
        smsReceiver.setOTPListener(this)
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsReceiver, intentFilter)

        val smsRetrieverClient = SmsRetriever.getClient(this)
        val task = smsRetrieverClient.startSmsRetriever()
        task.addOnSuccessListener {
            Logger.d("task - addOnSuccessListener - success!")
        }
        task.addOnFailureListener { e ->
            Logger.e("task - addOnFailureListener error: ${e.message}")
        }
    }

    private fun initPrefix() {
        val signature = appSignatureHelper.getAppSignature()[0]
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val country = telephonyManager.simCountryIso.toUpperCase()
        val prefix = PhoneNumberUtil.getInstance().getCountryCodeForRegion(country)
        binding.tvPrefix.text = getString(R.string.txt_prefix, prefix)
        Log.d("TAG", "App signature is: $signature")
    }

    private fun initBinding() {
        binding.viewModel = generateCodeViewModel
        regionAdapter = RegionAdapter(generateCodeViewModel, dataManager)
    }

    private fun initObservers() {
        generateCodeViewModel.mutableRegionList.observe(this, Observer {
            regionAdapter.submitList(it)
        })

        generateCodeViewModel.onCountryCodeClicked.observe(this, Observer {
            showAllCountryToUser()
        })

        generateCodeViewModel.onVerifyPhoneNumberClicked.observe(this, Observer {
            generateCodeViewModel.setCountryPrefix(getPrefixWithoutPlusSign())
            generateCodeViewModel.generateVerificationCode()
        })

        generateCodeViewModel.errorVerifyNumber.observe(this, Observer {
            showInvalidPhoneNumberErrorToUser(it)
        })

        generateCodeViewModel.generateCodeSuccessListener.observe(this, Observer {
            openCheckCodeActivity(it)
        })

        generateCodeViewModel.generateCodeErrorListener.observe(this, Observer {
            showGenerateCodeErrorToUser()
        })
    }

    private fun showAllCountryToUser() {
        val dialog = AlertDialog.Builder(this).create()
        val view = LayoutInflater.from(this).inflate(R.layout.all_regions, null)
        dialog.setView(view)
        val regionRV = view.findViewById<RecyclerView>(R.id.rv_all_regions)
        regionRV.adapter = regionAdapter
        regionRV.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                object : OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        dialog.dismiss()
                        val region = regionAdapter.currentList[position]
                        onRegionClicked(region)
                    }

                })
        )
        dialog.show()
    }

    private fun onRegionClicked(region: Region) {
        binding.tvPrefix.text = getString(R.string.txt_prefix, region.countryCode)
    }

    private fun showInvalidPhoneNumberErrorToUser(error: Boolean) {
        binding.tvEnterValidNumber.isVisible = error
        if (error)
            binding.tvPhoneNumberText.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
        else
            binding.tvPhoneNumberText.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryDark
                )
            )
    }

    override fun onOTPReceived(otp: String) {
        showToast("OTP received: $otp")
        unregisterReceiver(smsReceiver)
    }

    override fun onOTPTimeOut() {
        showToast("Error: Time out!!")
    }

    override fun onOTPError(error: String) {
        showToast(error)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getPrefixWithoutPlusSign(): String {
        val prefix = binding.tvPrefix.text
        return prefix.substring(1)
    }

    private fun openCheckCodeActivity(open: Boolean) {
        if (open) {
            openActivity(CheckCodeActivity::class.java)
            finish()
        } else
            showGenerateCodeErrorToUser()
    }

    private fun showGenerateCodeErrorToUser() {
        val snackBar = Snackbar.make(
            this.findViewById(android.R.id.content),
            R.string.txt_something_went_wrong,
            Snackbar.LENGTH_LONG
        )
        val view = snackBar.view
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        val text = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        text.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackBar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver)
    }
}