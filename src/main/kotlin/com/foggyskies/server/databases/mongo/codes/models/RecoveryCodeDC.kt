package com.foggyskies.server.databases.mongo.codes.models

import org.bson.codecs.pojo.annotations.BsonId

data class RecoveryCodeDC(
    @BsonId
    val id: String,
    val idUser: String
)
