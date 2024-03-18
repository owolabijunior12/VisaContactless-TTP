package com.lovisgod.testVisaTTP

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import com.lovisgod.testVisaTTP.domain.HorizonAppContainer
import com.pixplicity.easyprefs.library.Prefs

object SoftApplication {

     fun onCreate(context: Context) {

        Prefs.Builder()
            .setContext(context)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName("com.lovisgod.isw_visa_ttp")
            .setUseDefaultSharedPreference(true)
            .build()
    }

    object container {
        var horizonAppContainer = HorizonAppContainer()
        var horizonPayUseCase = horizonAppContainer.getUseCases()
    }

}