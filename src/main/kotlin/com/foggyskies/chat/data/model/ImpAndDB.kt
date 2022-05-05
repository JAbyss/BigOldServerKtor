package com.foggyskies.chat.data.model

import org.litote.kmongo.coroutine.CoroutineDatabase

data class ImpAndDB<I>(
    val db: CoroutineDatabase,
    val impl: I
)