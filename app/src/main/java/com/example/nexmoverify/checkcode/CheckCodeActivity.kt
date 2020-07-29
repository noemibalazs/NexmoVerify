package com.example.nexmoverify.checkcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
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
    }

    private fun checkIsValidOTP(validOtp: Boolean) {
        if (validOtp) {
            informUserPhoneNumberVerifiedAutomatically()
            openGuardianActivity()
        }
    }

    private fun informUserPhoneNumberVerifiedAutomatically() {
        val view = LayoutInflater.from(this).inflate(R.layout.custom_toast, customToast)
        val toast = Toast(this)
        toast.view = view
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    private fun openGuardianActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            openActivity(GuardianActivity::class.java)
            finish()
        }, 1200L)
    }
}