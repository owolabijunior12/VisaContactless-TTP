package com.lovisgod.testVisaTTP.models.datas

import android.os.Parcel
import android.os.Parcelable


class RequestIccData(
        var TRANSACTION_AMOUNT: String = "",

        var ANOTHER_AMOUNT: String = "000000000000",

        var APPLICATION_INTERCHANGE_PROFILE: String = "",

        var APPLICATION_TRANSACTION_COUNTER: String = "",

        var AUTHORIZATION_REQUEST: String = "",

        var CRYPTOGRAM_INFO_DATA: String = "",

        var CARD_HOLDER_VERIFICATION_RESULT: String = "",

        var ISSUER_APP_DATA: String = "",

        var TRANSACTION_CURRENCY_CODE: String = "",

        var TERMINAL_VERIFICATION_RESULT: String = "",

        var TERMINAL_COUNTRY_CODE: String = "",

        var TERMINAL_TYPE: String = "",

        var TERMINAL_CAPABILITIES: String = "",

        var TRANSACTION_DATE: String = "",

        var TRANSACTION_TYPE: String = "",

        var UNPREDICTABLE_NUMBER: String = "",

        var DEDICATED_FILE_NAME: String = ""): Parcelable {


        var INTERFACE_DEVICE_SERIAL_NUMBER: String = ""
        var APP_VERSION_NUMBER: String = ""
        var APP_PAN_SEQUENCE_NUMBER: String = ""
        var CARD_HOLDER_NAME: String = ""

        var iccAsString: String = ""

        var EMV_CARD_PIN_DATA: EmvPinData = EmvPinData()


        var haspin: Boolean? = true

        var TRACK_2_DATA: String = ""

        constructor(parcel: Parcel) : this(
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString()
        ) {
                INTERFACE_DEVICE_SERIAL_NUMBER = parcel.readString().toString()
                APP_VERSION_NUMBER = parcel.readString().toString()
                APP_PAN_SEQUENCE_NUMBER = parcel.readString().toString()
                CARD_HOLDER_NAME = parcel.readString().toString()
                iccAsString = parcel.readString().toString()
                haspin = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
                TRACK_2_DATA = parcel.readString().toString()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(TRANSACTION_AMOUNT)
                parcel.writeString(ANOTHER_AMOUNT)
                parcel.writeString(APPLICATION_INTERCHANGE_PROFILE)
                parcel.writeString(APPLICATION_TRANSACTION_COUNTER)
                parcel.writeString(AUTHORIZATION_REQUEST)
                parcel.writeString(CRYPTOGRAM_INFO_DATA)
                parcel.writeString(CARD_HOLDER_VERIFICATION_RESULT)
                parcel.writeString(ISSUER_APP_DATA)
                parcel.writeString(TRANSACTION_CURRENCY_CODE)
                parcel.writeString(TERMINAL_VERIFICATION_RESULT)
                parcel.writeString(TERMINAL_COUNTRY_CODE)
                parcel.writeString(TERMINAL_TYPE)
                parcel.writeString(TERMINAL_CAPABILITIES)
                parcel.writeString(TRANSACTION_DATE)
                parcel.writeString(TRANSACTION_TYPE)
                parcel.writeString(UNPREDICTABLE_NUMBER)
                parcel.writeString(DEDICATED_FILE_NAME)
                parcel.writeString(INTERFACE_DEVICE_SERIAL_NUMBER)
                parcel.writeString(APP_VERSION_NUMBER)
                parcel.writeString(APP_PAN_SEQUENCE_NUMBER)
                parcel.writeString(CARD_HOLDER_NAME)
                parcel.writeString(iccAsString)
                parcel.writeValue(haspin)
                parcel.writeString(TRACK_2_DATA)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<RequestIccData> {
                override fun createFromParcel(parcel: Parcel): RequestIccData {
                        return RequestIccData(parcel)
                }

                override fun newArray(size: Int): Array<RequestIccData?> {
                        return arrayOfNulls(size)
                }
        }

}

