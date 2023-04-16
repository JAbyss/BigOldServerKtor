package com.foggyskies.server.databases.mongo.newmessage.models

import com.foggyskies.server.databases.message.models.ChatMessageDC
import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class WatchNewMessage(
    @BsonId
    val idChat: String,
    val image: String,
    val username: String,
    val new_message: ChatMessageDC
)