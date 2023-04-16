package com.foggyskies.server.databases.mongo.codes.models

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class InfoLockDC(
    @BsonId
    val id: String,
    val reason: String,
    val time_to_block: String
)
