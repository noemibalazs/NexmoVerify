package com.example.nexmoverify.di

import org.koin.core.module.Module

class KoinInjector {

    companion object {

        fun injectModules(): MutableList<Module> {
            fun getAppSignatureModule() = listOf(appSignatureModule)
            fun getDataManagerModule() = listOf(dataManagerModule)
            fun getRegionManagerModule() = listOf(regionManagerModule)
            fun getGenerateCodeViewModelModule() = listOf(generateCodeViewModelModule)
            fun getNexmoModule() = listOf(nexmoModule)
            fun getTextBeltModule() = listOf(textBeltModule)
            fun getCheckCodeViewModelModule() = listOf(checkCodeViewModelModule)

            return mutableListOf<Module>().apply {
                addAll(getAppSignatureModule())
                addAll(getDataManagerModule())
                addAll(getRegionManagerModule())
                addAll(getGenerateCodeViewModelModule())
                addAll(getNexmoModule())
                addAll(getTextBeltModule())
                addAll(getCheckCodeViewModelModule())
            }
        }
    }
}