package com.foggyskies.chat.databases.main.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Token(
    @BsonId
    var id: String = ObjectId().toString(),
    var idUser: String,
    var username: String
)