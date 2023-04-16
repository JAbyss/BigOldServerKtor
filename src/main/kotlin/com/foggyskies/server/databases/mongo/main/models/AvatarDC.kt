package com.foggyskies.server.databases.mongo.main.models

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class AvatarDC(
    @BsonId
    var idUser: String,
    var image: String = ""
)