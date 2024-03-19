package com.lovisgod.testVisaTTP.models.datas

import android.content.Context
import com.lovisgod.testVisaTTP.SDKHelper
import com.lovisgod.testVisaTTP.models.enums.KeyType


data class EmvPinData (
    var ksn : String = SDKHelper.getKey(KeyType.KSN, SDKHelper.context as Context),
    var CardPinBlock: String = "")