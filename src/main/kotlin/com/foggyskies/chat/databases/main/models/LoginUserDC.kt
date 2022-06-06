package com.foggyskies.chat.data.model

@kotlinx.serialization.Serializable
data class LoginUserDC(
    var username: String,
    var password: String
)