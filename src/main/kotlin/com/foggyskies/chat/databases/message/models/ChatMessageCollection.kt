package com.foggyskies.chat.databases.message.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class ChatMessageCollection(
    @BsonId
    @SerialName(value = "_id")
    var id: String = ObjectId().toString(),
    var idUser: String,
    var date: String,
    var message: String,
    var listImages: List<String> = emptyList(),
    var listFiles: List<FileDC> = emptyList()
) {
    fun toCMDC(): ChatMessageDC {
        return ChatMessageDC(
            id = id,
            idUser = idUser,
            author = "",
            date = date,
            message = message,
            listImages = listImages,
            listFiles = listFiles
        )
    }
}

@Serializable
data class FileDC(
    val name: String,
    val size: String = "",
    val type: String = "",
    val path: String = ""
)

@Serializable
data class ChatMessageDC(
    var id: String = ObjectId().toString(),
    var idUser: String,
    var author: String,
    var date: String,
    var message: String,
    var listImages: List<String> = emptyList(),
    var listFiles: List<FileDC> = emptyList()
)