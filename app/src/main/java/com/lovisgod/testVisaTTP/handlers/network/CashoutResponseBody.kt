package com.lovisgod.testVisaTTP.handlers.network

import java.util.*

data class CashoutResponseBody(
 var statusCode : Int?    = null,
 var message    : String? = null,
 var data       : CashoutResponseData?   = CashoutResponseData()
)

data class CashoutResponseData(
    var amount                 : String? = null,
    var description            : String?  = null,
    var responseCode           : String?  = null,
    var authCode               : String?  = null,
    var referenceNumber        : String?  = null,
    var stan                   : String?  = null,
    var transactionChannelName : String?  = null,
    var wasReceive             : Boolean? = null,
    var wasSend                : Boolean? = null,
    var responseMessage        : String?  = null,
    var transactionRef         : String?  = null,
    var date                   : Any?     = null,
    var scripts                : String?  = null,
    var responseDescription    : String?  = null,
    var transactionId          : String?  = null,
    var transTYpe              : String?  = "Withdrawal",
    var paymentType            : String?  = "PAYMENT",
    var transactionTime        : String?  = null,
    var transactionDate        : String?  = null,
    var transactionDateTime    : String?  = null,
    var hasPinValue            : Boolean? = false,
    var remoteResponseCode     : String?  = null,
    var txnDate                : Long = Date().time
)
