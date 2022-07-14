package com.foggyskies.chat.data.model

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val idUser: String,
    val username: String,
    val sessionId: String,
    val socket: WebSocketSession
)