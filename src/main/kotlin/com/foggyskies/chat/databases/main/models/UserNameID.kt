package com.foggyskies.chat.databases.main.models

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class UserNameID(
    var id: String,
    var username: String
)