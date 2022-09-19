package com.foggyskies.server.databases.mongo.main.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class BlockUserDC(
    @BsonId
    val id: String = ObjectId().toString(),
    val lock_code: String,
    val time_lock: String,
    val time_unlock: String
)
