package com.lovisgod.testVisaTTP

import NFCListener
import android.content.Context
import android.nfc.NfcAdapter
import androidx.core.content.ContextCompat
import com.lovisgod.testVisaTTP.handlers.AesCryptoManager
import com.lovisgod.testVisaTTP.handlers.ISO_0_PinHelper
import com.lovisgod.testVisaTTP.handlers.Implementations.NFCListenerImpl
import com.lovisgod.testVisaTTP.handlers.NewNfcTransceiver
import com.lovisgod.testVisaTTP.models.datas.EmvPinData
import com.lovisgod.testVisaTTP.models.datas.RequestIccData
import com.lovisgod.testVisaTTP.models.datas.getIccData
import com.lovisgod.testVisaTTP.models.datas.getIccString
import com.lovisgod.testVisaTTP.models.enums.KeyMode
import com.lovisgod.testVisaTTP.models.enums.KeyType
import com.pixplicity.easyprefs.library.Prefs
import com.visa.app.ttpkernel.ContactlessConfiguration
import com.visa.app.ttpkernel.ContactlessKernel
import com.visa.vac.tc.emvconverter.Utils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object SDKHelper {


    val cryptoManager = AesCryptoManager()

    var context: Any? = null

    var lastRequestIccData: RequestIccData? = null

    var contactlessConfiguration: ContactlessConfiguration? = null

    private var nfcAdapter: NfcAdapter? = null
    var nfcListener: NFCListener? = null

    var newNfcTransceiver: NewNfcTransceiver? = null



    fun initialize(context: Context, nfcAdapter: NfcAdapter?) {
     this.context = context
     lastRequestIccData = null
        // Get the ContactlessConfiguration instance
        contactlessConfiguration = ContactlessConfiguration.getInstance()

        nfcListener = NFCListenerImpl()
        nfcListener?.activateNFC()

        newNfcTransceiver =  NewNfcTransceiver(nfcListener!!)

    }

    fun getTransactionData(data: HashMap<String, ByteArray>, pinBlock: EmvPinData): RequestIccData? {
      println(data)
      var value = ""

        for ((key1, value1) in data) {
            if (value1 != null) {
                value += Utils.getHexString(value1 as ByteArray?) as String
            }
        }

       println("iccData string gotten is ::::: $value")
        var iccdataBytes: ByteArray = Utils.hexToByteArray(value)
       val iccString = getIccString(iccdataBytes)
        println("iccData string needed and gotten is ::::: $iccString")

       val requestIccData = getIccData(iccdataBytes)
       println("iccData track 2  is :::: ${requestIccData.TRACK_2_DATA}")
       requestIccData.apply {
           this.iccAsString = iccString
           this.EMV_CARD_PIN_DATA = EmvPinData(CardPinBlock = pinBlock.CardPinBlock, ksn = pinBlock.ksn)
       }

       this.lastRequestIccData = requestIccData

        println("iccData pinblock is :::: ${requestIccData.EMV_CARD_PIN_DATA.CardPinBlock}")

        return lastRequestIccData
    }


    fun setPinMode(keyMode: KeyMode) {
      Prefs.putString(Constants.KEY_MODE, keyMode.name)
    }

    fun getPinMode(): KeyMode {
        val keyMode = Prefs.getString(Constants.KEY_MODE)
        return when(keyMode) {
            KeyMode.ISO_0.name -> KeyMode.ISO_0
            KeyMode.DUKPT.name -> KeyMode.DUKPT
            else -> {
                KeyMode.ISO_0
            }
        }
    }

    fun getPinBlock(clearPinText: String, pan: String, context: Context): String {

        // get the current pinmode
        val pinMode = getPinMode()

        // get pin key type
        val pinKeyType = when (pinMode) {
            KeyMode.ISO_0 -> KeyType.PIN_KEY
            KeyMode.DUKPT -> KeyType.IPEK
        }

        // get pin key
        val pinkey = getKey(pinKeyType, context)

        println("pin key is:::: $pinkey")

        return  ISO_0_PinHelper.getPinBlock(pan, pinkey, clearPinText)
    }

    fun injectKey(plainKey : String, context: Context, keyType: KeyType): Boolean {
        try {

            val bytes = plainKey.encodeToByteArray()
            val file = File(context.filesDir, getKeyTypeName(keyType))
            if (!file.exists()) {
                file.createNewFile()
            }
            val fos = FileOutputStream(file)
            cryptoManager.encrypt( // this method does the encryption and saves
                // it into an output stream which in turn saves it into the file
                bytes, fos
            ).decodeToString() // encrypt and convert to string
//            Prefs.putBoolean(LocalConstants.PASSCODE_CREATED, true)
            return true
        }catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getKey(keyType: KeyType, context: Context): String {
       try {
           val file = File(context.filesDir, getKeyTypeName(keyType))
           val decryptedString = cryptoManager.decrypt(FileInputStream(file)).decodeToString()

           return decryptedString
       }catch (
          e: Exception
       ) {
           e.printStackTrace()
           return ""
       }
    }

    fun getKeyTypeName(keyType: KeyType) : String {
       return  when (keyType) {
          KeyType.PIN_KEY -> "PinKey.txt"
          KeyType.MASTER_KEY -> "MasterKey.txt"
          KeyType.SESSION_KEY -> "SessionKey.txt"
          KeyType.IPEK -> "Ipek.txt"
          KeyType.KSN -> "Ksn.txt"
        }
    }

    /**
     * This method returns the next STAN (System Trace Audit Number)
     */
    fun getNextKsnCounter(): String {
        var ksn = Prefs.getInt("KSNCOUNTER", 0)

        // compute and save new stan
        val newKsn = if (ksn >= 9) 1 else ++ksn
        Prefs.putInt("KSNCOUNTER", ksn)

        return newKsn.toString()
    }
}