package com.lovisgod.testVisaTTP.models.uiState

import com.lovisgod.testVisaTTP.models.OnlineRespEntity
import com.lovisgod.testVisaTTP.models.datas.RequestIccData
import com.lovisgod.testVisaTTP.models.enums.AccountType
import com.lovisgod.testVisaTTP.models.enums.TransactionResultCode


interface ReadCardStates {

    fun onInsertCard()

    fun onCardDetected() {
        println("card has been detected")
    }
    fun onRemoveCard()

    fun onPinText(text: String = "") {
        println("pin text ::: $text")
    }

    fun onTransactionFailed(reason: String) {
        println(reason)
    }
    fun onPinInput()
    fun sendTransactionOnline(emvData: RequestIccData): OnlineRespEntity
    fun onEmvProcessing(message: String = "Please wait while we read card")
    fun onEmvProcessed(data: Any?, code: TransactionResultCode)
    fun onSelectAccountType(): AccountType

    fun onCardRead(cardType: String, cardNo: String) {
        println("$cardType card has been read")
    }
}