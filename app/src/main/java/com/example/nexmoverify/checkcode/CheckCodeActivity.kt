package com.example.nexmoverify.checkcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.nexmoverify.R
import com.example.nexmoverify.databinding.ActivityCheckCodeBinding
import com.example.nexmoverify.guardian.GuardianActivity
import com.example.nexmoverify.helper.DataManager
import com.example.nexmoverify.util.openActivity
import kotlinx.android.synthetic.main.custom_toast.*
import org.koin.android.ext.android.inject

class CheckCodeActivity : AppCompatActivity() {

    private val checkCodeViewModel: CheckCodeViewModel by inject()
    private val dataManager: DataManager by inject()
    private lateinit var binding: ActivityCheckCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_code)

        initBinding()
        initObservers()
    }

    private fun initBinding() {
        binding.viewModel = checkCodeViewModel
        binding.tvPhoneNumber.text =
            getString(R.string.txt_user_phone_number, dataManager.getUserPhoneNumber())
    }

    private fun initObservers() {
        checkCodeViewModel.mutableIsValidOTP.observe(this, Observer {
            checkIsValidOTP(it)
        })

        checkCodeViewModel.allDigitsAreIntroduced.observe(this, Observer {
            checkOTPByUser(it)
        })

        checkCodeViewModel.mutableIsValidOTPFromUI.observe(this, Observer {
            checkIsValidOTPFromUI(it)
        })
    }

    private fun checkIsValidOTP(validOtp: Boolean) {
        if (validOtp) {
            informUserPhoneNumberVerified(getString(R.string.txt_phone_number_automatically_verified))
            openGuardianActivity()
        }
    }

    private fun informUserPhoneNumberVerified(text:String) {
        val view = LayoutInflater.from(this).inflate(R.layout.custom_toast, customToast)
        val toast = Toast(this)
        toast.view = view
        val toastText = view.findViewById<AppCompatTextView>(R.id.tv_toast)
        toastText.text = text
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    private fun openGuardianActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            openActivity(GuardianActivity::class.java)
            finish()
        }, 2100L)
    }

    private fun checkOTPByUser(digitsAreIntroduced: Boolean) {
        if (digitsAreIntroduced) {
            checkCodeViewModel.verifyUserCode(readOTPFromUI())
        } else {
            binding.tvWrongCode.isVisible = !digitsAreIntroduced
        }
    }

    private fun readOTPFromUI(): String {
        val firstDigit = binding.etFirstDigit.text.toString()
        val secondDigit = binding.etSecondDigit.text.toString()
        val thirdDigit = binding.etThirdDigit.text.toString()
        val forthDigit = binding.etForthDigit.text.toString()
        val fifthDigit = binding.etFifthDigit.text.toString()
        val sixthDigit = binding.etSixthDigit.text.toString()
        return getString(
            R.string.txt_number_six_digit_code,
            firstDigit,
            secondDigit,
            thirdDigit,
            forthDigit,
            fifthDigit,
            sixthDigit
        )
    }

    private fun checkIsValidOTPFromUI(validOtp: Boolean) {
        if (validOtp) {
            informUserPhoneNumberVerified(getString(R.string.txt_phone_number_verified))
            openGuardianActivity()
        }else{
            binding.tvWrongCode.isVisible = !validOtp
        }
    }
}