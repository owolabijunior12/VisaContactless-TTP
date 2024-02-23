import android.nfc.Tag

interface NFCListener {
    fun activateNFC()
    fun deactivateNFC()

    @Throws(Exception::class)
    fun transceiveApdu(var1: ByteArray?): ByteArray?
    fun resetNFCField(): Boolean
    fun setTimeout(var1: Int)
    fun onNfcTagDiscovered(var1: Tag?)
}