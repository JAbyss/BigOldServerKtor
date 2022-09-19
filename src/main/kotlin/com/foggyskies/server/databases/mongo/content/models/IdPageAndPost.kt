package com.foggyskies.server.databases.mongo.content.models

@kotlinx.serialization.Serializable
data class IdPageAndPost(
    val idPageProfile: String,
    val idPost: String
)