package com.foggyskies.server.databases.mongo.main.models

@kotlinx.serialization.Serializable
data class UserNameID(
    var id: String,
    var username: String
)