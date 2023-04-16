package com.foggyskies.server.data.model

@kotlinx.serialization.Serializable
data class CreateChat(
    var username: String,
    var idUserSecond: String
)