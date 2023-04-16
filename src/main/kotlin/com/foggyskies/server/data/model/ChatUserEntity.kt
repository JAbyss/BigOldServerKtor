package com.foggyskies.server.data.model

import org.litote.kmongo.Data

@Data
@kotlinx.serialization.Serializable
data class ChatUserEntity(
    var idUser: String,
//    var nameUser: String,
    var notifiable: String = ""
)