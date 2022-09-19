package com.foggyskies.server.databases.mongo.main.models

@kotlinx.serialization.Serializable
data class UserIUSI(
    var id: String,
    var username: String,
    var status: String,
    var image: String
)
