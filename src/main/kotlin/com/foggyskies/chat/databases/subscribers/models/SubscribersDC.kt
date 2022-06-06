package com.foggyskies.chat.databases.subscribers.models

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class SubscribersDC(
    @BsonId
    var id: String,
    var isNotifiable: String
)
