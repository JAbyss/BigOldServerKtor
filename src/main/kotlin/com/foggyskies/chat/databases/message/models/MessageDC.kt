package com.foggyskies.chat.databases.message.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageDC(
    var listImages: List<String> = emptyList(),
    var listFiles: List<FileDC> = emptyList(),
    var message: String
)