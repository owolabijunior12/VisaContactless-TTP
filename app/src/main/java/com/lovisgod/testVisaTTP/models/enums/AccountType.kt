package com.lovisgod.testVisaTTP.models.enums

/**
 * This enum type identifies the
 * different Bank Account types
 */
enum class AccountType(val value: String) {
    Default("00"),
    Savings("10"),
    Current("20"),
    Credit("30")
}