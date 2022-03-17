package com.jetbrains.handson.chat.server.chat.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Token(
    @BsonId
    var id: String = ObjectId().toString(),
    var username: String
)