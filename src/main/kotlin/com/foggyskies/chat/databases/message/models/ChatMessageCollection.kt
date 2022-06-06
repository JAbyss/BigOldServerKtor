package com.foggyskies.chat.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class ChatMessageCollection(
    @BsonId
    @SerialName(value = "_id")
    var id: String = ObjectId().toString(),
//    var author: String,
    var idUser: String,
    var date: String,
    var message: String,
    var listImages: List<String> = emptyList()
){
    fun toCMDC(): ChatMessageDC {
        return ChatMessageDC(
            id = id,
            idUser = idUser,
            author = "",
            date = date,
            message = message,
            listImages = listImages
        )
    }
}

@Serializable
data class ChatMessageDC(
//    @BsonId
    var id: String = ObjectId().toString(),
//    var author: String,
    var idUser: String,
    var author: String,
    var date: String,
    var message: String,
    var listImages: List<String> = emptyList()
)