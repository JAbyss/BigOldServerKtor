package com.foggyskies.chat.data.model

@kotlinx.serialization.Serializable
data class UserNameID(
    var id: String,
    var username: String
)