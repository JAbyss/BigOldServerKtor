package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.collections.getFiftyMessage
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.getNewMessagesByIdChat
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getMessages(isCheckToken: Boolean) = cRoute(
    path = "/getMessages",
    method = HttpMethod.Get,
    isCheckToken
){token ->

    val idChat = call.request.headers["idChat"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest)

    val messages = MessagesDataBase.Messages.getFiftyMessage(idChat)

   call.respond(status = HttpStatusCode.OK, messages)
}