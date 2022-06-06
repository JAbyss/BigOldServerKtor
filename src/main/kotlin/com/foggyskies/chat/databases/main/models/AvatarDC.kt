package com.foggyskies.chat.databases.main.models

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class AvatarDC(
    @BsonId
    var idUser: String,
    var image: String
)
