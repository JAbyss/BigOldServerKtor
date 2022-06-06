package com.foggyskies.chat.data.model

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class UserNameID(
    var id: String,
    var username: String
)