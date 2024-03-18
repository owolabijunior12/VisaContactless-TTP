package com.lovisgod.testVisaTTP.handlers

import NFCListener
import com.lovisgod.testVisaTTP.Constants.lastReadTag
import com.visa.app.ttpkernel.NfcTransceiver

class NewNfcTransceiver(val nfcListener: NFCListener) : NfcTransceiver {
        override fun transceive(txData: ByteArray): ByteArray {
            println("this is getting here for transceive")
            val cmd = HexUtil.toHexString(txData)
            var resp: String? = null

            println("\nPOS: $cmd")

            while (lastReadTag == null) {
                Thread.sleep(1000)
            }
//            lastReadTag?.connect()
            val apduRes = nfcListener.transceiveApdu(txData)
            resp = HexUtil.toHexString(apduRes)
            println("\nCARD: $resp\n")
            return apduRes!!
        }

        override fun destroy() {}
        override fun isCardPresent(): Boolean {
            return true
        }
    }