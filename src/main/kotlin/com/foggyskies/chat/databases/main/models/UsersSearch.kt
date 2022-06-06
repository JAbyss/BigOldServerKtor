package com.jetbrains.handson.chat.server.chat.data.model

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class UsersSearch(
    @BsonId
    var id: String,
    var username: String,
    var image: String,
    var status: String,
    var isFriend: Boolean,
    var awaitAccept: Boolean
)
