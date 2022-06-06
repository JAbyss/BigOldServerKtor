package com.foggyskies.chat.data.model

import com.foggyskies.chat.databases.content.models.ContentUsersDC
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class ContentRequestDC(
    val idPageProfile: String,
    var item: NewContentDC
)

@kotlinx.serialization.Serializable
data class NewContentDC(
    var type: String,
    var value: String,
    var description: String
){
    fun toNewPost(nameFile: String): ContentUsersDC {
        return ContentUsersDC(
            id = ObjectId().toString(),
            type = type,
            likes = emptyList(),
            comments = emptyList(),
            address = nameFile,
            description = description
        )
    }
}
