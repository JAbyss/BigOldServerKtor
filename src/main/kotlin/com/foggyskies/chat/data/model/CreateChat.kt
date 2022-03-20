package com.foggyskies.chat.data.model

@kotlinx.serialization.Serializable
data class CreateChat(
    var username: String,
    var idUserSecond: String
)