package com.lovisgod.testVisaTTP.data


import android.content.Context
import com.lovisgod.testVisaTTP.SDKHelper
import com.lovisgod.testVisaTTP.SDKHelper.contactlessConfiguration
import com.lovisgod.testVisaTTP.models.datas.ConfigInfoHelper.saveTerminalInfo
import com.lovisgod.testVisaTTP.models.datas.IswHpCodes
import com.lovisgod.testVisaTTP.models.datas.TerminalInfo
import com.lovisgod.testVisaTTP.models.enums.KeyType


class EmvDataKeyManager {

    // Get the ContactlessConfiguration instance
    fun setEmvConfig(terminalInfo: TerminalInfo) {
        saveTerminalInfo(terminalInfo)
        val myData = contactlessConfiguration?.terminalData
//        myData?.set("9F02", "100".encodeToByteArray()) // set the amount

        myData?.set("9F1A", terminalInfo.terminalCountryCode.encodeToByteArray()) // set terminal country code

        myData?.set("5F2A", terminalInfo.transCurrencyCode.encodeToByteArray()) // set currency code

        myData?.set("9F35", byteArrayOf(0x22)) //Terminal Type

//        myData?.set("9C", byteArrayOf(0x00)) //Transaction Type 00 - Purchase; 20 - Refund

        myData?.set("9F66", byteArrayOf(0xE6.toByte(), 0x00.toByte(), 0x40.toByte(), 0x00.toByte())) //TTQ E6004000

        myData?.set("9F39",  byteArrayOf(0x07))                          //POS Entry Mode

        //myData.put("9F39", new byte[]{0x07});                               //POS Entry Mode
        myData?.set("9F33", terminalInfo.terminalCapabilities.encodeToByteArray()) //Terminal Capabilities

        myData?.set("9F40", byteArrayOf(
            0x60.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x50.toByte(),
            0x01.toByte()
        )
        ) //Additional Terminal Capabilities

        val merchant = "${terminalInfo.cardAcceptorNameLocation}"
        val merchant_byte = merchant.toByteArray()
        myData?.set("9F4E", merchant_byte) //Merchant Name and location


        //default is 99 99 99 99 99 99 99
        //Kernel SDK will used DF01 instead of terminal floor limit

        //default is 99 99 99 99 99 99 99
        //Kernel SDK will used DF01 instead of terminal floor limit
        myData?.set("9F1B", byteArrayOf(0x00, 0x00, 0x00, 0x00)) //terminal floor limit

        myData?.set("DF01", byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x01)) //Reader CVM Required Limit

        contactlessConfiguration?.terminalData = myData
    }


    private fun setDukpt(key: String, ksn:String): Int {
       try {
         SDKHelper.injectKey(key, SDKHelper.context as Context, KeyType.IPEK)
         SDKHelper.injectKey(ksn, SDKHelper.context as Context, KeyType.KSN)
         return 0
       }catch (e: Exception) {
           e.printStackTrace()
           return IswHpCodes.PIN_LOAD_ERROR
       }
    }

    fun setPinKey(isDukpt: Boolean, key: String = "", ksn: String = ""): Int {
        if (isDukpt){
            return  setDukpt(key, ksn)
        } else {
          var result = setMasterKey(key)
          if (result == 0) {
              result = setPinWorkingKey(key, 0)
          }
          return result
        } // implement pin key later
    }

    fun setMasterKey(key: String): Int {
      try {
          SDKHelper.injectKey(key, SDKHelper.context as Context, KeyType.MASTER_KEY)
          return 0
      } catch (e: Exception) {
          e.printStackTrace()
          return IswHpCodes.PIN_LOAD_ERROR
      }
    }

    fun setPinWorkingKey(key: String, keyIndex: Int): Int {
        try {
            SDKHelper.injectKey(key, SDKHelper.context as Context, KeyType.PIN_KEY)
            return 0
        } catch (e: Exception) {
            e.printStackTrace()
            return IswHpCodes.PIN_LOAD_ERROR
        }
    }
}