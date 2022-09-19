package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.collections.editMessage
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.editMessage
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.chat.EditMessageEntity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.editMessage(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ChatRoute.editMessage,
    method = HttpMethod.Post,
    isCheckToken
) { token ->

    val editMessageEntity = call.receive<EditMessageEntity>()

    val code = editMessage(editMessageEntity)

    call.respondText(status = HttpStatusCode.OK, text = code.toString())
}

suspend fun editMessage(editMessageEntity: EditMessageEntity): Boolean {
    return if (!MessagesDataBase.Messages.editMessage(editMessageEntity.idChat, editMessageEntity.idMessage, editMessageEntity.newMessage))
        NewMessagesDataBase.NewMessages.editMessage(editMessageEntity)
    else
        true
}