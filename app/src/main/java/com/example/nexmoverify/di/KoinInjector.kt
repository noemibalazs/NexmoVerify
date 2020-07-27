package com.example.nexmoverify.di

import org.koin.core.module.Module

class KoinInjector {

    companion object {

        fun injectModules(): MutableList<Module> {
            fun getAppSignatureModule() = listOf(appSignatureModule)
            fun getDataManagerModule() = listOf(dataManagerModule)
            fun getRegionManagerModule() = listOf(regionManagerModule)
            fun getRegionViewModelModule() = listOf(regionViewModelModule)
            fun getNexmoModule() = listOf(nexmoModule)

            return mutableListOf<Module>().apply {
                addAll(getAppSignatureModule())
                addAll(getDataManagerModule())
                addAll(getRegionManagerModule())
                addAll(getRegionViewModelModule())
                addAll(getNexmoModule())
            }
        }
    }
}