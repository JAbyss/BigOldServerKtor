package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.chat.roomsChats
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createChatSession(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ChatRoute.createChatSession,
    method = HttpMethod.Get,
    isCheckToken
) { token ->

    val idChat =
        call.parameters["idChat"] ?: return@cRoute call.respond(
            HttpStatusCode.BadRequest,
            "Параметр idChat не получен."
        )

    roomsChats[idChat] = mutableListOf()

    if (idChat.isNotEmpty()) {
        //TODO Socket надо разобраться
//        createSocket(idChat, sessionsChat)
        call.respond(HttpStatusCode.Created)
    }
}

fun <T> MutableList<T>.addIfNotExist(newValue: T) {

    for (item in this) {
        if (item == newValue)
            return
    }
    this.add(newValue)
}