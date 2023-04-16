package com.foggyskies.server.data.model

import com.foggyskies.server.databases.mongo.content.models.ContentUsersDC
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
    fun toNewPost(): ContentUsersDC {
        return ContentUsersDC(
            id = ObjectId().toString(),
            type = type,
            likes = emptyList(),
            comments = emptyList(),
            address = value,
            description = description
        )
    }
}
