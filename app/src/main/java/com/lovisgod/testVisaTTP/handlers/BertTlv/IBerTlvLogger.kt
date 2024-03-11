package com.lovisgod.testVisaTTP.handlers.BertTlv

interface IBerTlvLogger {
    val isDebugEnabled: Boolean
    fun debug(aFormat: String?, vararg args: Any?)
}