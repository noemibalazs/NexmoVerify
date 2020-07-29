package com.example.nexmoverify.di

import com.example.nexmoverify.checkcode.CheckCodeViewModel
import com.example.nexmoverify.helper.DataManager
import com.example.nexmoverify.nexmo.NexmoApiClient
import com.example.nexmoverify.otp.AppSignatureHelper
import com.example.nexmoverify.region.RegionManager
import com.example.nexmoverify.generate.GenerateCodeViewModel
import com.example.nexmoverify.textbelt.TextBeltApiClient
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appSignatureModule = module {
    single { AppSignatureHelper(androidApplication().applicationContext) }
}

val dataManagerModule = module {
    single { DataManager(androidApplication().applicationContext) }
}

val regionManagerModule = module {
    single { RegionManager(PhoneNumberUtil.getInstance()) }
}

val generateCodeViewModelModule = module {
    viewModel {
        GenerateCodeViewModel(
            appSignatureHelper = get(),
            regionManager = get(),
            dataManager = get(),
            context = androidApplication().applicationContext,
            textBeltApiClient = get()
        )
    }
}

val nexmoModule = module {
    single { NexmoApiClient.getNexmoApiClient() }
}

val textBeltModule = module {
    single { TextBeltApiClient.getTextBeltApiClient() }
}

val checkCodeViewModelModule = module {
    viewModel {
        CheckCodeViewModel(
            appSignatureHelper = get(),
            dataManager = get(),
            textBeltApiClient = get()
        )
    }
}