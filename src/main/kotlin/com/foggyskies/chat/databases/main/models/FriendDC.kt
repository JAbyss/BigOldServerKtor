package com.foggyskies.chat.data.model

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class FriendDC(
    @BsonId
    var idUser: String,
    var friends: List<UserNameID>
)