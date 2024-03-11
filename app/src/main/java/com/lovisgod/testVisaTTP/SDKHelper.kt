package com.lovisgod.testVisaTTP

import android.content.Context
import androidx.core.content.ContextCompat
import com.lovisgod.testVisaTTP.handlers.AesCryptoManager
import com.lovisgod.testVisaTTP.models.enums.KeyType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object SDKHelper {

    val cryptoManager = AesCryptoManager()

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
        }
    }
}