package com.foggyskies.chat.data.model

@kotlinx.serialization.Serializable
data class FormattedChatDC(
    var id: String,
    var nameChat: String,
    var idCompanion: String,
    var image: String,
    var lastMessage: String
)