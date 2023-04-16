package com.foggyskies.server.routes.chat.sockets

import com.foggyskies.ServerDate
import com.foggyskies.server.databases.message.models.ChatMessageCollection
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.collections.insertOne
import com.foggyskies.server.plugin.SystemRouting.ChatRoute.chatSessions
import com.foggyskies.server.plugin.cWS
import com.foggyskies.server.routes.chat.ChatUserAndSocket
import com.foggyskies.server.routes.chat.requests.addIfNotExist
import com.foggyskies.server.routes.chat.requests.saveAsNewMessage
import com.foggyskies.server.routes.chat.roomsChats
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.chatSession() = cWS(
    path = chatSessions,
    isCheckToken = true
) { token ->

    val idChat = call.request.headers["idChat"] ?: return@cWS call.respond(HttpStatusCode.BadRequest)

    onJoin(idChat, token.idUser, this)

    incoming.consumeEach { frame ->
        val message = (frame as Frame.Text).readText()
        val formattedMessage = ChatMessageCollection(
            idUser = token.idUser,
            date = ServerDate.fullDate,
            message = message
        )
        sendAllinChat(idChat, formattedMessage)
        if (roomsChats.size in 0..1)
            saveAsNewMessage(
                idChat,
                token.idUser,
                formattedMessage
            )
        else
            saveMessage(idChat, formattedMessage)
    }

    onDisconnect(idChat, token.idUser)

    println("Я закрываюсь")
}

private fun onJoin(idChat: String, idUser: String, socket: DefaultWebSocketSession) {
    if (roomsChats[idChat] == null)
        roomsChats[idChat] = mutableListOf(ChatUserAndSocket(idUser, socket))
    else
        roomsChats[idChat]?.addIfNotExist(ChatUserAndSocket(idUser, socket))
}

private suspend fun saveMessage(idChat: String, message: ChatMessageCollection) {
    MessagesDataBase.Messages.insertOne(idChat, message)
}

private suspend fun sendAllinChat(idChat: String, message: ChatMessageCollection) {
    val jsonMessage = Json.encodeToString(message)
    roomsChats[idChat]?.forEach {
        it.socket.send(jsonMessage)
    }
}

private fun onDisconnect(idChat: String, idUser: String) {
    roomsChats[idChat]?.let { list ->
        list.removeIf { it.idUser == idUser }
        if (list.size == 0) {
            roomsChats.remove(idChat)
        }
    }
}