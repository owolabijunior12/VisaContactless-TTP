# SoftPOS SDK for Android

This SDK enables integration of SoftPOS functionality into Android applications. SoftPOS (Software Point of Sale) allows Android devices to function as secure card payment terminals.

## Installation

To integrate the SoftPOS SDK into your Android application, follow these steps:

1. Add the SoftPOS SDK dependency to your project.

   ```gradle
2. Call the following method in the onCreate method of your Application class:
  ```kotlin
  SoftApplication.onCreate(this.applicationContext)
  ```
3. Set up NFC in your application by calling the setupNfc method in your activity or any class of your choice:
  ```kotlin
fun setupNfc() {
    val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    SDKHelper.initialize(this.applicationContext, nfcAdapter!!)
}
```
4. Handle the NFC reader mode in the onResume method of your activity:
   ```kotlin
   override fun onResume() {
    super.onResume()
    SDKHelper.nfcListener?.let {
        val NFC_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS
        nfcAdapter!!.enableReaderMode(this, it::onNfcTagDiscovered, NFC_FLAGS, null)
    }}
    ```
5. Handle closing the NFC adapter interface in the onPause and onDestroy lifecycle methods:
    ```kotlin
   override fun onPause() {
    super.onPause()
    SDKHelper.nfcListener?.resetNFCField()
    nfcAdapter!!.disableReaderMode(this)
    }

    override fun onDestroy() {
       super.onDestroy()
    SDKHelper.nfcListener?.resetNFCField()
   }
   ```
6. Before performing transactions, perform key injection for the desired key mode:
    ```kotlin
   SoftApplication.container.horizonPayUseCase.setPinKeyUseCase(isDukpt = false, key = "0000000000000000000000", ksn = "")
   ```
7. Extend the TransactionLogger and ReadCardStates interfaces in your activity or class:
    ```kotlin
   class MainActivity : AppCompatActivity(), TransactionLogger, ReadCardStates {
    // Implementation of interface methods...}
   ```
8. Implement the necessary methods for transaction handling in your activity:
   ```kotlin
     override fun onCardRead(cardType: String, cardNo: String) {
    // Implementation logic...
         // }
   override fun sendTransactionOnline(emvData: RequestIccData): OnlineRespEntity {
   // Implementation logic...
   }
   // Implement other interface methods...
   ```
9. Perform transactions by calling appropriate methods:
   ```kotlin
     runBlocking {
       useCases.emvPayUseCase.invoke("100".toInt().toLong(), this@MainActivity, this@MainActivity)
     }
   ```
10. Run your application and test the SoftPOS functionality.

# EXAMPLES
```kotlin
  class MainActivity : AppCompatActivity(), TransactionLogger, ReadCardStates {
   // Implementation of activity methods...
}

class SampleApplication: Application() {
   override fun onCreate() {
      super.onCreate()
      SoftApplication.onCreate(this.applicationContext)
   }
}

```

### This markdown README provides comprehensive guidance for integrating the SoftPOS SDK into Android applications. It includes step-by-step instructions, code snippets, and example implementations to facilitate the integration process for developers.
