package com.foggyskies.server.data.model

import io.ktor.websocket.*
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val idUser: String,
    val username: String,
    val sessionId: String,
    val socket: WebSocketSession
)