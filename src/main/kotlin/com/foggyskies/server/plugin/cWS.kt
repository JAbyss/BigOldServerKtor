package com.foggyskies.server.plugin

import com.foggyskies.server.databases.mongo.main.models.Token
import com.foggyskies.server.infixfun.ch
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import com.foggyskies.server.plugin.verifications.checkToken

fun Route.cWS(
    path: String,
    isCheckToken: Boolean,
    body: suspend DefaultWebSocketServerSession.(Token) -> Unit
) = webSocket(path) {

    val token = (isCheckToken ch ::checkToken) ?: return@webSocket
    body(this, token)
}