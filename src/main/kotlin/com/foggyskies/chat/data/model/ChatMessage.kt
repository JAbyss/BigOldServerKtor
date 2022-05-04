package com.jetbrains.handson.chat.server.chat.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class ChatMessage(
    @BsonId
    var id: String = ObjectId().toString(),
    var author: String,
    var date: String,
    var message: String,
    var listImages: List<String> = emptyList()
)