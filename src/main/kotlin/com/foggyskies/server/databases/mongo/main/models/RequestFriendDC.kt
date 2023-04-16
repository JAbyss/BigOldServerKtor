package com.foggyskies.server.databases.mongo.main.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class RequestFriendDC(
    @BsonId
    var id: String = ObjectId().toString(),
    var requests: List<String>
)