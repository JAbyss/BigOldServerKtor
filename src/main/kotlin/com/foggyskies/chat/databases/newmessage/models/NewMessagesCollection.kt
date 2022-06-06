package com.foggyskies.chat.databases.newmessage.models

import com.foggyskies.chat.databases.message.models.ChatMessageCollection
import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class NewMessagesCollection(
    @BsonId
    val id: String,
    val new_messages: List<ChatMessageCollection>
)