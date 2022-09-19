package com.foggyskies.server.databases.mongo.main.models

@kotlinx.serialization.Serializable
data class FormattedChatDC(
    var id: String,
    var nameChat: String,
    var idCompanion: String,
    var image: String,
    var lastMessage: String
)