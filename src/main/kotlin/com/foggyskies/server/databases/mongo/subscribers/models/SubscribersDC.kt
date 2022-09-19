package com.foggyskies.server.databases.mongo.subscribers.models

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class SubscribersDC(
    @BsonId
    var id: String,
    var isNotifiable: String
)
