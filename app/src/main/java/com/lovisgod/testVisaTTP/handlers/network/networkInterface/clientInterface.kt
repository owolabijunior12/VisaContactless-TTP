package com.lovisgod.testVisaTTP.handlers.network.networkInterface


import com.lovisgod.testVisaTTP.handlers.network.simplecalladapter.Simple
import com.lovisgod.testVisaTTP.handlers.network.CashoutRequestBody
import com.lovisgod.testVisaTTP.handlers.network.CashoutResponseBody
import com.lovisgod.testVisaTTP.handlers.network.UpKeyDownloadResponse
import retrofit2.http.*

interface clientInterface {

    @GET("/get-up-key")
    fun getUpKeyAndData(@Query("terminalId") terminalId: String):
            Simple<UpKeyDownloadResponse>

    @GET("/get-nibss-keys")
    fun getNibssKeyAndData(@Query("terminalId") terminalId: String):
            Simple<UpKeyDownloadResponse>
}


interface PaymentClientInterface {

    @POST("/perform-purchase-transaction")
    fun makePurchaseToNibss(@Body cashoutRequestBody: CashoutRequestBody, @Query("version") version: String): Simple<CashoutResponseBody>


}
