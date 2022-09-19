package com.foggyskies.server.databases.message.models

import com.foggyskies.ServerDate
import kotlinx.serialization.Serializable

@Serializable
data class MessageDC(
    var listImages: List<String> = emptyList(),
    var listFiles: List<FileDC> = emptyList(),
    var message: String = ""
){
    fun toChatMessage(idUser: String): ChatMessageCollection {
        return ChatMessageCollection(
            date = ServerDate.fullDate,
            message = message,
            listFiles = listFiles,
            listImages = listImages,
            idUser = idUser
        )
    }
}