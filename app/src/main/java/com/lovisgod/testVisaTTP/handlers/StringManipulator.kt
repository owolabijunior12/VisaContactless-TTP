package com.lovisgod.testVisaTTP.handlers

object StringManipulator{
    fun dropLastCharacter(values: String): String {
        if (values.isNotEmpty()) {
            val xxx = values.dropLast(1)
            return  xxx
        } else {
            return values
        }
    }


    fun dropFirstCharacter(values: String, toDrop: Int): String {
        if (values.isNotEmpty()) {
            val xxx = values.drop(toDrop)
            println("this is usable string : ${xxx}")
            return xxx
        } else {
            return values
        }

    }
}