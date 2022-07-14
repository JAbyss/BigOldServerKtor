package com.foggyskies.chat.extendfun


fun Number.toBoolean(): Boolean =
    when (this) {
        0 -> false
        1 -> true
        else -> throw Exception("Value is not 0 or 1")

    }