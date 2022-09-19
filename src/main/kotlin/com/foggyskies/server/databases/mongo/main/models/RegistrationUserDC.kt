package com.foggyskies.server.databases.mongo.main.models

@kotlinx.serialization.Serializable
data class RegistrationUserDC(
    var username: String,
    var password: String,
    var e_mail: String
)