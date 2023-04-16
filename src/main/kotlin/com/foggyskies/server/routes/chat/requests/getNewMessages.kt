package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getChatById
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.clearOneChat
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.getNewMessagesByIdChat
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getNewMessages(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ChatRoute.getNewMessages,
    method = HttpMethod.Get,
    isCheckToken
) { token ->

    val idChat = call.request.headers["idChat"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest)

    var newMessages = NewMessagesDataBase.NewMessages.getNewMessagesByIdChat(idChat, token.idUser)

    if (newMessages.isEmpty()) {
        val chat = MainDataBase.Chats.getChatById(idChat)
        val secondUser =
            if (chat.firstCompanion?.idUser == token.idUser) chat.secondCompanion?.idUser!! else chat.firstCompanion?.idUser!!
        newMessages = NewMessagesDataBase.NewMessages.getNewMessagesByIdChat(idChat, secondUser)
    } else
        NewMessagesDataBase.NewMessages.clearOneChat(idChat, token.idUser)


    call.respond(status = HttpStatusCode.OK, newMessages)
}