package com.foggyskies.chat.databases.newmessage.models

import com.foggyskies.chat.databases.message.models.ChatMessageDC
import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class WatchNewMessage(
    @BsonId
    val idChat: String,
    val image: String,
    val username: String,
    val new_message: ChatMessageDC
)