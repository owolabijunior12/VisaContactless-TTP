package com.lovisgod.testVisaTTP.handlers

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object ISO_0_PinHelper {

    fun newXorhex(pinBlock: String, panBlock: String): String {
        val pinBlockBigInt = pinBlock.toBigInteger(16)
        val panBlockBigInt = panBlock.toBigInteger(16)

        return "0${(pinBlockBigInt.xor(panBlockBigInt)).toString(16)}"
    }

    fun encryptTripleDES(data: ByteArray, key: ByteArray): ByteArray {


        val tmp: ByteArray = key
        val keyBytes = ByteArray(24)
        System.arraycopy(tmp, 0, keyBytes, 0, 16)
        System.arraycopy(tmp, 0, keyBytes, 16, 8)
        val sk: SecretKey = SecretKeySpec(keyBytes, "DESede")

        // do the decryption with that key

        // do the decryption with that key
        val cipher = Cipher.getInstance("DESede/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, sk)
        return cipher.doFinal(data)
    }

    fun generatePinBlock(pin: String, cardNumber: String): String {
        println(cardNumber)

        if (pin.length < 4 || pin.length > 6) {
            throw Exception("Invalid pin length")
        }

        var pinBlock = "0${pin.length}$pin"

        while (pinBlock.length != 16) {
            pinBlock += 'F'
        }

        val MAX_PAN_LENGTH = 12
        val cardLen = cardNumber.length
        val pan = "0000${cardNumber.substring(cardLen - MAX_PAN_LENGTH - 1, cardLen - 1)}"

        return newXorhex(pinBlock, pan)
    }

    fun getFormat0PinBlock(key: String, xoredPinBlock: String) : String {
        println("xoredpinblock:::: $xoredPinBlock")
        val pinBlockBytes = HexUtil.parseHex(xoredPinBlock)
        val keyBytes = HexUtil.parseHex(key)
        val result = encryptTripleDES(pinBlockBytes, keyBytes)
        val stringResult = HexUtil.toHexString(result)

        println("pinblock hex:::::: $stringResult")

        return stringResult
    }

    fun getPinBlock(pan: String, key: String, clearPin: String): String {
        val xoredPinBlock = generatePinBlock(clearPin, pan)

        return getFormat0PinBlock(key, xoredPinBlock)
    }



}