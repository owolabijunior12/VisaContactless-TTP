package com.lovisgod.testVisaTTP.handlers.network

data class UpKeyDownloadResponse(
   var statusCode : Int?    = null,
   var message    : String? = null,
   var data       : KeyData?  = KeyData()
)

data class KeyData (
    var sessionKey : String? = null,
    var masterKey  : String? = null,
    var pinKey     : String? = null,
    var params     : UpParams? = UpParams()
)

data class UpParams(
    var statusCode : Int?    = null,
    var message    : String? = null,
    var data       :  ParamsData?   = ParamsData()
)

data class ParamsData(
   var terminalCode             : String? = null,
   var cardAcceptorId           : String? = null,
   var merchantId               : String? = null,
   var merchantName             : String? = null,
   var merchantAddress1         : String? = null,
   var merchantAddress2         : String? = null,
   var merchantPhoneNumber      : String? = null,
   var merchantEmail            : String? = null,
   var merchantState            : String? = null,
   var tmsRouteType             : String? = null,
   var merchantCity             : String? = null,
   var qtbMerchantCode          : String? = null,
   var qtbMerchantAlias         : String? = null,
   var cardAcceptorNameLocation : String? = null,
   var merchantCategoryCode     : String? = null,
   var terminalCountryCode      : String? = null,
   var transCurrencyCode        : String? = null,
   var transCurrencyExp         : String? = null,
   var terminalType             : String? = null,
   var terminalCapabilities     : String? = null,
   var terminalExtCapabilities  : String? = null,
   var terminalEntryMode        : String? = null,
   var nibbsKey                 : String? = null,
   var upkey                    : String? = null
)
