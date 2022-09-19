package com.foggyskies.server.data.bettamodels

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class NotificationDocument(
    @BsonId
    var id: String,
    var notifications: List<Notification>
)

@kotlinx.serialization.Serializable
data class Notification(
    var id: String,
    var idUser: String,
    var title: String,
    var description: String,
    var image: String,
    var status: String
)