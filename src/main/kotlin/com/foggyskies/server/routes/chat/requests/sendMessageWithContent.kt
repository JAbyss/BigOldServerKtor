package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.databases.message.models.ChatMessageCollection
import com.foggyskies.server.databases.message.models.MessageDC
import com.foggyskies.server.databases.mongo.testpacage.Logger
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getChatById
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.collections.insertOne
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.insertOneMessage
import com.foggyskies.server.extendfun.getSizeFile
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.chat.roomsChats
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun Route.sendMessageWithContent(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ChatRoute.sendMessageWithContent,
    method = HttpMethod.Post,
    isCheckToken
) { token ->

    val message = call.receive<MessageDC>()
//    Logger.addLog(hashCode().toString(), message, Logger.StatusCodes.INFO)
    val idChat = call.request.headers["idChat"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest)
//    Logger.addLog(hashCode().toString(), idChat, Logger.StatusCodes.INFO)

    val formattedMessage = message.toChatMessage(token.idUser)

    formattedMessage.listFiles.forEach {
        val file = File(it.path)
        it.size = getSizeFile(file.length())
        it.type = file.extension
    }

    roomsChats[idChat]?.forEach {
        val jsonMessage = Json.encodeToString(formattedMessage)
        it.socket.send(jsonMessage)
    }
    if (roomsChats.size in 0..1)
        saveAsNewMessage(
            idChat,
            token.idUser,
            formattedMessage
        )
    else
        saveMessage(idChat, formattedMessage)

    call.respond(HttpStatusCode.OK)
}

suspend fun saveMessage(idChat: String, message: ChatMessageCollection) {
    MessagesDataBase.Messages.insertOne(
        idChat,
        message
    )
}

suspend fun saveAsNewMessage(idChat: String, idUserSender: String, message: ChatMessageCollection) {
    val chat = MainDataBase.Chats.getChatById(idChat)
    val userReceiver =
        if (chat.firstCompanion?.idUser == idUserSender) chat.secondCompanion?.idUser!! else chat.firstCompanion?.idUser!!
    NewMessagesDataBase.NewMessages.insertOneMessage(idChat, userReceiver, message)
}