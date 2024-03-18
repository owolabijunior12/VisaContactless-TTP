package com.lovisgod.testVisaTTP.handlers.Implementations

import NFCListener
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import com.lovisgod.testVisaTTP.Constants
import com.lovisgod.testVisaTTP.Constants.lastReadTag
import com.lovisgod.testVisaTTP.handlers.HexUtil
import java.io.IOException

class NFCListenerImpl : NFCListener {
//    private var lastTagRead: IsoDep? = null
    override fun activateNFC() {}
    override fun deactivateNFC() {
        try {
            if (lastReadTag != null) {
                lastReadTag!!.close()
                lastReadTag?.close()
            }
            lastReadTag = null
        } catch (e: IOException) {
            Log.e(TAG, "activateNFC: ", e)
        }
    }

//    val cardStatus: CardStatus
//        get() = if (lastTagRead != null && lastTagRead!!.isConnected) {
//            CardStatus.PRESENT_ON_FIELD
//        } else CardStatus.ABSENT_OFF_FIELD

    @Throws(Exception::class)
    override fun transceiveApdu(capdu: ByteArray?): ByteArray {
        val rapdu = lastReadTag!!.transceive(capdu)
        Log.i("C-APDU", HexUtil.toHexString(capdu))
        Log.i("R-APDU", HexUtil.toHexString(rapdu))

//        if (ApduUtils.isChangeApplicationPriorityCommand(capdu)) {
//            ApduUtils.changeKernelApplicationPriority(rapdu);
//        }
        return rapdu
    }

    override fun resetNFCField(): Boolean {
        deactivateNFC()
        activateNFC()
        return true
    }

    override fun setTimeout(timeout: Int) {
        lastReadTag!!.timeout = timeout
    }

    override fun onNfcTagDiscovered(tag: Tag?) {
        println("this is discovered :: $tag")
        lastReadTag = IsoDep.get(tag)
        try {
            lastReadTag?.connect()
        } catch (e: IOException) {
            Log.e(TAG, "onNfcTagRead: ", e)
        }
        lastReadTag?.timeout = 3000
    }

    companion object {
        private val TAG = NFCListenerImpl::class.java.simpleName
    }
}