package com.foggyskies.chat.data.model

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class AvatarDC(
    @BsonId
    var idUser: String,
    var image: String
)
