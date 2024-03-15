package com.lovisgod.testVisaTTP.handlers.network.networkInterface

import android.content.Context
import com.lovisgod.testVisaTTP.Constants
import com.lovisgod.testVisaTTP.SDKHelper
import com.lovisgod.testVisaTTP.handlers.network.CashoutRequestBody
import com.lovisgod.testVisaTTP.models.datas.RequestIccData
import com.lovisgod.testVisaTTP.models.enums.KeyType
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object networkSampleRepo {

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun downloadKeys(context: Context) {
        println("got here for download keys")
        val client = paybleClient().getClient()
        GlobalScope.launch {
            val response = client.getNibssKeyAndData(terminalId = "2ISW0001").run()
            if (response.isSuccessful) {
                Constants.SESSION_KEY = response.body()?.data?.sessionKey.toString()
                Constants.params = response.body()?.data?.params?.data
                SDKHelper.injectKey(response.body()?.data?.pinKey.toString(), context, KeyType.PIN_KEY)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun testTrans(context: Context, requestIccData: RequestIccData) {
        val requestData = CashoutRequestBody(
            merchantCategoryCode = Constants.params?.merchantCategoryCode,
            terminalCode = "${Constants.params?.terminalCode}",
            merchantName = "${Constants.params?.merchantName}",
            merchantId = "${Constants.params?.merchantId}",
            haspin = requestIccData.EMV_CARD_PIN_DATA.CardPinBlock.isNotEmpty(),
//            haspin = false,
            track2Data = requestIccData.TRACK_2_DATA,
            panSequenceNumber = requestIccData.APP_PAN_SEQUENCE_NUMBER,
            amount = "100",
            pinBlock = requestIccData.EMV_CARD_PIN_DATA.CardPinBlock,
//            pinBlock = "",
            posDataCode = "510101511344101",
            iccString = requestIccData.iccAsString,
            agentTransType = "push"
        )
        val client = paybleClient().getPaymentMiddleWareClient()
        GlobalScope.launch {
            val response = client.makePurchaseToNibss(cashoutRequestBody = requestData, version = "1").run()
            if (response.isSuccessful) {
                println(response.body()?.data?.responseCode)
            }
        }
    }
}