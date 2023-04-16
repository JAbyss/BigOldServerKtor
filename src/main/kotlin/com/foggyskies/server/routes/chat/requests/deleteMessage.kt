package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.collections.deleteMessage
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.collections.getMessageById
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.deleteNewMessage
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.getMessageById
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.chat.DeleteMessageEntity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.deleteMessage(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ChatRoute.deleteMessage,
    method = HttpMethod.Post,
    isCheckToken
) { token ->

    val deleteMessageEntity = call.receive<DeleteMessageEntity>()
//    Logger.addLog(hashCode().toString(), deleteMessageEntity.json, Logger.StatusCodes.INFO)
    val fullMessage =
        MessagesDataBase.Messages.getMessageById(deleteMessageEntity.idChat, deleteMessageEntity.idMessage)
            ?: NewMessagesDataBase.NewMessages.getMessageById(
                idUser = deleteMessageEntity.idUser,
                idChat = deleteMessageEntity.idChat,
                idMessage = deleteMessageEntity.idMessage
            )
//    Logger.addLog(hashCode().toString(), fullMessage.json, Logger.StatusCodes.INFO)

    fullMessage.listFiles.forEach { File(it.path).delete() }
    fullMessage.listImages.forEach { File(it).delete() }

    val code = deleteMessage(deleteMessageEntity)

    call.respondText(status = HttpStatusCode.OK, text = code.toString())
}

private suspend fun deleteMessage(deleteMessageDC: DeleteMessageEntity): Int {
    return if (MessagesDataBase.Messages.deleteMessage(deleteMessageDC.idChat, deleteMessageDC.idMessage) == 0)
        NewMessagesDataBase.NewMessages.deleteNewMessage(
            deleteMessageDC.idUser,
            deleteMessageDC.idChat,
            deleteMessageDC.idMessage
        )
    else
        1
}