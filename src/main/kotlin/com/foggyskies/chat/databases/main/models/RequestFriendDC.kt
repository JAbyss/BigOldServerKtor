package com.foggyskies.chat.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class RequestFriendDC(
    @BsonId
    var id: String = ObjectId().toString(),
    var requests: List<UserNameID>
)