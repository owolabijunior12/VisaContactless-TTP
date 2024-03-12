package com.lovisgod.testVisaTTP

import android.app.Application
import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Prefs.Builder()
            .setContext(this.applicationContext)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName("com.lovisgod.isw_visa_ttp")
            .setUseDefaultSharedPreference(true)
            .build()
    }
}