package com.foggyskies.chat.databases.main.models

@kotlinx.serialization.Serializable
data class FriendListDC(
    var id: String,
    var username: String,
    var status: String,
    var image: String
)