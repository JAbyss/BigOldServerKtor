package com.foggyskies.chat.data.model

@kotlinx.serialization.Serializable
data class ChatPreviewEntity(
    var idChat: String,
    var chatName: String,
    var image: String,
    var lastMessage: String
)