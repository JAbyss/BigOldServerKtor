package com.foggyskies.server.databases.mongo.codes.models

import org.bson.codecs.pojo.annotations.BsonId

data class LockCodeDC(
    @BsonId
    val id: String,
    val idUser: String,
    val lock_code: String
)