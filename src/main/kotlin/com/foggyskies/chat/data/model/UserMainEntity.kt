package com.foggyskies.chat.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class UserMainEntity(
    @BsonId
    var idUser: String = ObjectId().toString(),
    var username: String,
    var e_mail: String,
    var image: String = "",
    var status: String = "",
    var chats: List<String> = emptyList(),
    val password: String
)