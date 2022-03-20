package com.foggyskies.chat.data.model

import org.litote.kmongo.Data

@Data
@kotlinx.serialization.Serializable
data class ChatUserEntity(
    var idUser: String,
    var nameUser: String

)