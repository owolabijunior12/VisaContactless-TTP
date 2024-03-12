package com.lovisgod.testVisaTTP

import android.nfc.Tag
import android.nfc.tech.IsoDep

object Constants {

    var testCase = 0
    val testCase_ONLINE_PIN = 2
    val testCase_SIGNATURE = 3
    val testCase_REFUND = 4
    var lastReadTag : IsoDep? = null
    var KEY_MODE = "KEY_MODE"
}