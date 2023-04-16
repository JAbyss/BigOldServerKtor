package com.foggyskies.server.databases.mongo.codes.models

import org.bson.codecs.pojo.annotations.BsonId

data class VerifyCodeDC(
    @BsonId
    val email: String,
    val code: String
)
