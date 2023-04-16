package com.foggyskies.server.databases.mongo.main.models

import com.foggyskies.server.data.model.ChatUserEntity
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Data

@Data(internal = true)
@kotlinx.serialization.Serializable
data class ChatMainEntity(
    @BsonId
    var idChat: String = ObjectId().toString(),
    var firstCompanion: ChatUserEntity?,
    var secondCompanion: ChatUserEntity?
)