package com.foggyskies.server.databases.mongo.main.models

@kotlinx.serialization.Serializable
data class LoginUserDC(
    var username: String,
    var password: String
)