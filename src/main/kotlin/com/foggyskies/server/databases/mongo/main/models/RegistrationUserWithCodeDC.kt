package com.foggyskies.server.databases.mongo.main.models

@kotlinx.serialization.Serializable
data class RegistrationUserWithCodeDC(
    var username: String,
    var password: String,
    var e_mail: String,
    var code: String
)
