package com.foggyskies.chat.data.model

@kotlinx.serialization.Serializable
data class FriendListDC(
    var id: String,
    var username: String,
    var status: String,
    var image: String
)