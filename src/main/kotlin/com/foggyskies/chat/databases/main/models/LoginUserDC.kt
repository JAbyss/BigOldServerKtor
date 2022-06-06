package com.foggyskies.chat.databases.main.models

@kotlinx.serialization.Serializable
data class LoginUserDC(
    var username: String,
    var password: String
)