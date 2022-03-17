package com.jetbrains.handson.chat.server.chat.data.model

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val username: String,
    val sessionId: String,
    val socket: WebSocketSession
)