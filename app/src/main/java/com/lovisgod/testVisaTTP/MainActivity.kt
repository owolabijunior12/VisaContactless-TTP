package com.lovisgod.testVisaTTP

import NFCListener
import android.annotation.SuppressLint
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.lovisgod.nfcpos.utils.BerTag
import com.lovisgod.testVisaTTP.Constants.lastReadTag
import com.lovisgod.testVisaTTP.Constants.testCase
import com.lovisgod.testVisaTTP.Constants.testCase_ONLINE_PIN
import com.lovisgod.testVisaTTP.Constants.testCase_REFUND
import com.lovisgod.testVisaTTP.Constants.testCase_SIGNATURE
import com.lovisgod.testVisaTTP.handlers.Conversions
import com.lovisgod.testVisaTTP.handlers.HexUtil
import com.lovisgod.testVisaTTP.handlers.Implementations.NFCListenerImpl
import com.lovisgod.testVisaTTP.handlers.KeyBoardClick
import com.lovisgod.testVisaTTP.handlers.PPSEManager
import com.lovisgod.testVisaTTP.handlers.PinKeyPadHandler
import com.lovisgod.testVisaTTP.handlers.TlvData
import com.lovisgod.testVisaTTP.models.enums.KeyMode
import com.lovisgod.testVisaTTP.models.enums.KeyType
import com.visa.app.ttpkernel.ContactlessConfiguration
import com.visa.app.ttpkernel.ContactlessKernel
import com.visa.app.ttpkernel.NfcTransceiver
import com.visa.app.ttpkernel.TtpOutcome
import com.visa.app.ttpkernel.Version
import com.visa.vac.tc.emvconverter.Utils
import java.io.IOException
import java.util.Arrays
import java.util.HashMap


class MainActivity : AppCompatActivity(), TransactionLogger, KeyBoardClick {

    private var nfcAdapter: NfcAdapter? = null
    var nfcListener: NFCListener? = null

    var contactlessConfiguration: ContactlessConfiguration? = null
    var contactlessKernel: ContactlessKernel? = null
    var nfcTransceiver: MyNfcTransceiver? = null
    var newNfcTransceiver: NewNfcTransceiver? = null
    var mainLog: TextView? = null
    var testBtn: AppCompatButton? = null
    var view: View?  = null
    var DF03 = ""
    var clearPinText = ""
    var transactionContactlessResult: HashMap<String, ByteArray>? = null

    // Supported AIDs
    val supportedAid = ArrayList(
        Arrays.asList(
            "A0000000031010"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLog = findViewById(R.id.mainLog)
        testBtn = findViewById(R.id.testBtn)
        view = findViewById(R.id.keypad_layout)

        contactlessKernel = ContactlessKernel.getInstance(applicationContext);

        // Get the ContactlessConfiguration instance
        contactlessConfiguration = ContactlessConfiguration.getInstance()

        val myData = contactlessConfiguration?.terminalData
        myData?.set("9F02", byteArrayOf(0x01, 0x00, 0x00)) // set the amount

        myData?.set("9F1A", byteArrayOf(0x05, 0x66)) // set terminal country code

        myData?.set("5F2A", byteArrayOf(0x05, 0x66.toByte())) // set currency code

        myData?.set("9F35", byteArrayOf(0x22)) //Terminal Type

        myData?.set("9C", byteArrayOf(0x00)) //Transaction Type 00 - Purchase; 20 - Refund

        myData?.set("9F66", byteArrayOf(0xE6.toByte(), 0x00.toByte(), 0x40.toByte(), 0x00.toByte())) //TTQ E6004000

        //myData.put("9F39", new byte[]{0x07});                               //POS Entry Mode

        //myData.put("9F39", new byte[]{0x07});                               //POS Entry Mode
        myData?.set("9F33", byteArrayOf(0xE0.toByte(), 0xF8.toByte(), 0xC8.toByte())) //Terminal Capabilities

        myData?.set("9F40", byteArrayOf(
            0x60.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x50.toByte(),
            0x01.toByte()
        )
        ) //Additional Terminal Capabilities

        val merchant = "Visa TTP KOD"
        val merchant_byte = merchant.toByteArray()
        myData?.set("9F4E", merchant_byte) //Merchant Name and location


        //default is 99 99 99 99 99 99 99
        //Kernel SDK will used DF01 instead of terminal floor limit

        //default is 99 99 99 99 99 99 99
        //Kernel SDK will used DF01 instead of terminal floor limit
        myData?.set("9F1B", byteArrayOf(0x00, 0x00, 0x00, 0x00)) //terminal floor limit

        myData?.set("DF01", byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x01)) //Reader CVM Required Limit

        contactlessConfiguration?.terminalData = myData

        // Create new instance of the NFC transceiver

        // Create new instance of the NFC transceiver
        nfcTransceiver = MyNfcTransceiver()

        mainLog!!.textSize = 12f
        mainLog!!.gravity = Gravity.LEFT
        mainLog?.setText("")

        // set up nfc adapter and others

        PinKeyPadHandler.handleKeyButtonClick(this, view!!)

        SDKHelper.setPinMode(KeyMode.ISO_0)

        setupNfc()

        doDummyPinKey() //this is dummy pinkey loading for the purpose testing pinkey injection

        // For testing purposes, perform 10 transactions

        testBtn?.setOnClickListener {
//            testCase = 1
//            while (testCase <= 10) {
                doTransaction()
//                testCase++
//            }
        }

    }

    fun setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcListener = NFCListenerImpl()
        nfcListener?.activateNFC()

        newNfcTransceiver =  NewNfcTransceiver(nfcListener!!)
    }

    @SuppressLint("SetTextI18n")
    fun doTransaction() {
        val kernelVersion = Version.getVersion()
        mainLog!!.text = """${mainLog!!.text}
TestCase # $testCase  TTP KoD Kernel Ver: ${
            Utils.getHexString(
                kernelVersion
            )
        }"""

        // Select PPSE
        var selectedAid: ByteArray? = null
        val ppseManager = PPSEManager()
        try {
            // Set suppported AID
            ppseManager.setSupportedApps(supportedAid)

            // Perform Select PPSE
            selectedAid = ppseManager.selectPPSE(newNfcTransceiver)
            println("selected aid === $selectedAid")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        while (selectedAid != null) {
            // Get the ContactlessConfiguration instance
            contactlessConfiguration = ContactlessConfiguration.getInstance()

            // Specify the terminal settings for this transaction
            val myData = contactlessConfiguration?.getTerminalData()
            myData?.set("4F", selectedAid) // set the selected aid
            myData?.set("9F4E", byteArrayOf(
                0x00.toByte(),
                0x11.toByte(),
                0x22.toByte(),
                0x33.toByte(),
                0x44.toByte(),
                0x55.toByte(),
                0x66.toByte(),
                0x77.toByte(),
                0x88.toByte(),
                0x99.toByte(),
                0xAA.toByte(),
                0xBB.toByte(),
                0xCC.toByte(),
                0xDD.toByte(),
                0xEE.toByte(),
                0xFF.toByte()
            )
            )
            myData?.set("009C", byteArrayOf(0x20.toByte()))

            // Call TTP Kernel performTransaction
            val contactlessResult =
                contactlessKernel!!.performTransaction(newNfcTransceiver, contactlessConfiguration)

            // Check the transaction outcome
            val outcome = contactlessResult.finalOutcome
            mainLog!!.text = """
            ${mainLog!!.text}
            ContactlessResult.getFinalOutcome: 
            """.trimIndent()
            when (outcome) {
                TtpOutcome.COMPLETED -> {
                    this.log("Online Approval requested")
                    mainLog!!.text = """
     ${mainLog!!.text}
     Online Approval Requested
     """.trimIndent()
                }

                TtpOutcome.DECLINED -> {
                    this.log("Transaction Declined")
                    mainLog!!.text = """
     ${mainLog!!.text}
     Transaction Declined
     """.trimIndent()
                }

                TtpOutcome.ABORTED -> mainLog!!.text = """
     ${mainLog!!.text}
     Transaction Terminated
     """.trimIndent()

                TtpOutcome.TRYNEXT -> mainLog!!.text = """
     ${mainLog!!.text}
     PPSE:Try Next Application
     """.trimIndent()

                TtpOutcome.SELECTAGAIN -> mainLog!!.text = """
     ${mainLog!!.text}
     GPO Returned 6986. Application Try Again.
     """.trimIndent()

                else -> {}
            }

            // Display the TTP Kernel version
            mainLog!!.text = """
            ${mainLog!!.text}
            ContactlessResult.getKernelData: getVersion
            """.trimIndent()
            contactlessKernel!!.kernelData

            // Display the TTP Kernel data
            val version = contactlessKernel!!.kernelData
            var key: String
            var value: String
            var src: String
            for ((key1, value1)  in version) {
                if (value1 != null) {
                    key = key1 as String
                    value = Utils.getHexString(value1) as String
                    mainLog!!.text = """
                    ${mainLog!!.text}
                    $key:$value
                    """.trimIndent()
                }
            }

            // Display the transaction results
            mainLog!!.text = """
            ${mainLog!!.text}
            
            TLV data format 
            card data
            ContactlessResult.getData:
            """.trimIndent()
            val cardData = contactlessResult.data
            transactionContactlessResult = contactlessResult.data
            for ((key1, value1) in cardData) {
                key = key1 as String
                if (value1 != null) {
                    value = Utils.getHexString(value1 as ByteArray?) as String
                    mainLog!!.text = """
                    ${mainLog!!.text}
                    $key:$value
                    """.trimIndent()
                    this.log("""
                    $key:$value
                    """.trimIndent())
                }
            }
            mainLog!!.text = """
            ${mainLog!!.text}
            
            ContactlessResult.getInternalData():
            """.trimIndent()

            Log.d("ContactlessResult", "ContactlessResult getInternalData(): ")
            val internalData = contactlessResult.internalData
            for ((key1, value1) in internalData) {
                if (value1 != null) {
                    key = key1 as String
                    value = Utils.getHexString(value1) as String
                    mainLog!!.text = """
                    ${mainLog!!.text}
                    $key:$value
                    """.trimIndent()
                    //'DF01' Reader CVM Required Limit
                    //'DF03' CVM Kernel Outcome
                    //	'00' = CVM capture not required
                    //	'01' = Signature required
                    //	'02' = Online PIN required
                    //	'03' = CVM processing resulted in transaction decline
                }
            }



            // Display the last APDU sent & the last SW received by the TTP Kernel
            if (contactlessResult.lastApdu != null && contactlessResult.lastSW != null) {
                mainLog!!.text = """
                ${mainLog!!.text}
                
                Last APDU Command
                """.trimIndent()
                mainLog!!.text = """
                ${mainLog!!.text}
                ${Utils.getHexString(contactlessResult.lastApdu)}
                """.trimIndent()
                mainLog!!.text = """
                ${mainLog!!.text}
                
                Last APDU Response
                """.trimIndent()
                mainLog!!.text = """
                ${mainLog!!.text}
                ${Utils.getHexString(contactlessResult.lastSW)}
                
                
                
                
                """.trimIndent()
            }
            val df03tlv = internalData["DF03"]?.let { Conversions.parseBERTLV(it) }

            println(df03tlv?.find(BerTag("DF03"))?.getHexValue())

            DF03 = df03tlv?.find(BerTag("DF03"))?.getHexValue().toString()

            println("CVM kernel outcome ::: $DF03")

            if (DF03.isNotEmpty() && (DF03 == "02")) {
              view?.visibility = View.VISIBLE
            } else {
                println("this is the cvm result :::: $DF03")
            }

            // Check TTP Kernel transaction outcome
            selectedAid = if (outcome == TtpOutcome.TRYNEXT) {
                // Perform another transaction using next AID in the list
                ppseManager.nextCandidate()
            } else {
                null
            }
        }
    }

    // This is a dummy NFCTransceiver class which returns hardcoded CARD responses only.
    class MyNfcTransceiver : NfcTransceiver {
        override fun transceive(txData: ByteArray): ByteArray {
            val cmd = Utils.getHexString(txData)
            var resp: String? = null

            //CARD responses
            if (txData[0] == 0x00.toByte() && txData[1] == 0xA4.toByte() && txData[4] == 0x0E.toByte()) {
                //PPSE
                resp =
                    "6F39840E325041592E5359532E4444463031A527BF0C2461224F07A000000003101050105649534120434F4E544143544C4553538701019F2A01039000"

                // Online PIN Required
                if (testCase == testCase_ONLINE_PIN) {
                    resp =
                        "6F32840E325041592E5359532E4444463031A520BF0C1D611B4F07A000000003101050105649534120434F4E544143544C4553539000"
                    resp =
                        "6F32840E325041592E5359532E4444463031A520BF0C1D611B4F07A000000003101050105649534120434F4E544143544C4553539000"
                    resp =
                        "6F32840E325041592E5359532E4444463031A520BF0C1D611B4F07A000000003101050105649534120434F4E544143544C4553539000"
                     println("ONLINE PIN PPSE response \n")

                }

                // Signature Required
                if (testCase == testCase_SIGNATURE) {
                    resp =
                        "6F32840E325041592E5359532E4444463031A520BF0C1D611B4F07A000000003101050105649534120434F4E544143544C4553539000"
                    resp =
                        "6F32840E325041592E5359532E4444463031A520BF0C1D611B4F07A000000003101050105649534120434F4E544143544C4553539000"
                    println("Signature  PPSE response \n")
                }

                // Refund supported
                if (testCase == testCase_REFUND) {
                    resp =
                        "6F32840E325041592E5359532E4444463031A520BF0C1D611B4F07A000000003101050105649534120434F4E544143544C4553539000"
                    println( "REFUND PPSE response \n")
                }
            } else if (txData[0] == 0x00.toByte() && txData[1] == 0xA4.toByte() && txData[4] == 0x07.toByte() || txData[4] == 0x08.toByte() || txData[4] == 0x09.toByte()) {
                //SELECT
                resp =
                    "6F438407A0000000031010A53850105649534120434F4E544143544C4553539F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C089F5A0510084008409000"

                // Online PIN Required
                if (testCase == testCase_ONLINE_PIN) {
                    resp =
                        "6F438407A0000000031010A53850105649534120434F4E544143544C4553539F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C089F5A0510084008409000"
                    resp =
                        "6F438407A0000000031010A53850105649534120434F4E544143544C4553539F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C089F5A0510084008409000"
                    resp =
                        "6F438407A0000000031010A53850105649534120434F4E544143544C4553539F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C089F5A0510084008409000"
                   println("ONLINE PIN SELECT AID response \n")

                }

                // Signature Required
                if (testCase == testCase_SIGNATURE) {
                    resp =
                        "6F438407A0000000031010A53850105649534120434F4E544143544C4553539F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C089F5A0510084008409000"
                    resp =
                        "6F438407A0000000031010A53850105649534120434F4E544143544C4553539F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C089F5A0510084008409000"
                    println( "SIGNATURE SELECT AID response \n" )

                }

                // Refund supported
                if (testCase == testCase_REFUND) {
                    resp =
                        "6F438407A0000000031010A53850105649534120434F4E544143544C4553539F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C089F5A0510084008409000"
                   println("REFUND SELECT AID response \n")
                }
            } else if (txData[0] == 0x80.toByte() && txData[1] == 0xA8.toByte() && txData[2] == 0x00.toByte()) {
                //GPO
                resp =
                    "7781918202002094040805050057104761731000000027D2412201190582545F20135649534120434445542033302F4341524430325F3401019F10201F220100A00000000000000000000000000000000000000000000000000000009F260896991101193D90D29F2701809F360200019F6C0200009F6E04207000009F7C0C010A434152440244474991159F5D060000000000009000"
                //CARD EXPIRED
                resp =
                    "7781918202002094040805050057104761731000000027D2012201190582545F20135649534120434445542033302F4341524430325F3401019F10201F220100A00000000000000000000000000000000000000000000000000000009F260896991101193D90D29F2701809F360200019F6C0200009F6E04207000009F7C0C010A434152440244474991159F5D060000000000009000"

                // Online PIN Required
                if (testCase == testCase_ONLINE_PIN) {
                    resp =
                        "775857134761739001010010D30122011234599999991F9F2701809F260872655BE61E10321C9F360200015F3401009F6E04204000009F6C0280009F100706011203A00000820220205F200E4E616D6530696E205265636F72649000"
                    resp =
                        "775857134761739001010010D30122011234599999991F9F2701809F2608414FAF2C39D644109F360200015F3401009F6E04204000009F6C02C0009F100706011203A00000820220205F200E4E616D6530696E205265636F72649000"
                    resp =
                        "775857134761739001010010D30122011234599999991F9F2701809F260872655BE61E10321C9F360200015F3401009F6E04204000009F6C0280009F100706011203A00000820220205F200E4E616D6530696E205265636F72649000"
                    println( "ONLINE PIN GPO response \n" )
                }

                // Signature Required
                if (testCase == testCase_SIGNATURE) {
                    resp =
                        "775857134761739001010010D30122011234599999991F9F2701809F2608E8BF22459E066AC89F360200015F3401009F6E04204000009F6C02C0009F100706011203A00000820220205F200E4E616D6530696E205265636F72649000"
                    resp =
                        "775857134761739001010010D30122011234599999991F9F2701809F2608F224B701AF06B6229F360200015F3401009F6E04204000009F6C0240009F100706011203A00000820220205F200E4E616D6530696E205265636F72649000"
                    println( "SIGNATURE GPO response \n"
                    )
                }

                // Refund supported
                if (testCase == testCase_REFUND) {
                    resp =
                        "775857134761739001010010D30122011234599999991F9F2701009F2608DA19325C0FF809A29F360200015F3401009F6E04204000009F6C0200009F100706011203800000820220205F200E4E616D6530696E205265636F72649000"
                   println( "REFUND GPO response\n")
                }
            } else if (txData[0] == 0x00.toByte() && txData[1] == 0xB2.toByte()) {
                //READ RECORD
                resp = "700A5F280208409F0702C2809000"

                // Online PIN Required
                if (testCase == testCase_ONLINE_PIN) {
                    resp = ""
                   println("ONLINE PIN READ RECORD response \n"
                    )
                }

                // Signature Required
                if (testCase == testCase_SIGNATURE) {
                    resp = ""
                    println("SIGNATURE READ RECORD response \n"
                    )
                }

                // Refund supported
                if (testCase == testCase_REFUND) {
                    resp = ""
                   println( "REFUND READ RECORD response \n"
                    )
                }
               println(  "end READ RECORD response \n")
            }
            println("\nPOS: $cmd")
            println("\nCARD: $resp\n")
            return Utils.hexToByteArray(resp)
        }

        override fun destroy() {}
        override fun isCardPresent(): Boolean {
            return true
        }
    }


    class NewNfcTransceiver(val nfcListener: NFCListener) : NfcTransceiver {
        override fun transceive(txData: ByteArray): ByteArray {
            println("this is getting here for transceive")
            val cmd = Utils.getHexString(txData)
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

    override fun log(message: String) {
        println(message)
    }


    override fun onResume() {
        println(nfcListener == null)
        super.onResume()

        val NFC_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS
        nfcListener!!.let {
            nfcAdapter!!.enableReaderMode(this,
                it::onNfcTagDiscovered, NFC_FLAGS, null)
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter!!.disableReaderMode(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        nfcListener?.resetNFCField()
    }

    fun doDummyPinKey() {
        val pinKey = "45C36DD03F229F5DC1C662E2291CA2BA"
        SDKHelper.injectKey(pinKey, this.applicationContext, KeyType.PIN_KEY)
    }

    override fun onNumClick(num: String) {
      if (clearPinText.length > 6) {
          Toast.makeText(this, "Pin Cannot be more than 6", Toast.LENGTH_LONG).show()
      } else {
          clearPinText += num
          view?.findViewById<TextView>(R.id.pin_text)?.text = "*".repeat(clearPinText.length)
      }
    }

    override fun onSubmitButtonClick() {
        println("pin is ${clearPinText}")
        // calculate pin block based on the mode of pin
       if (clearPinText.length < 4) {
           Toast.makeText(this, "Pin not complete", Toast.LENGTH_LONG).show()
       } else {
           view?.visibility = View.GONE
           val track2  =  transactionContactlessResult?.get("57")?.let { Conversions.parseBERTLV(it)?.find(BerTag("57"))?.getHexValue() }
           println("track 2::::  $track2")
           val pan  = track2?.uppercase()?.split("D")?.get(0)

           println(pan)

           val pinBlock = SDKHelper.getPinBlock(clearPinText, pan.toString(), this)
       }
    }

    override fun onBackSpace() {
        if (clearPinText.isNotEmpty()) {
            clearPinText = clearPinText.substring(0, clearPinText.length -1)
            view?.findViewById<TextView>(R.id.pin_text)?.text = "*".repeat(clearPinText.length)
        }
    }

    override fun onClear() {
        clearPinText = ""
        view?.findViewById<TextView>(R.id.pin_text)?.text = "*".repeat(clearPinText.length)
    }
}