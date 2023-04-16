package com.foggyskies.server.databases.mongo.main.models

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class FriendDC(
    @BsonId
    var idUser: String,
    var friends: List<String>
)