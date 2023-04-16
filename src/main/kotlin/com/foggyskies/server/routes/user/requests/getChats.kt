package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getChatById
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getChatsByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getUsername
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.collections.getLastMessage
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.getNewMessagesByIdChat
import com.foggyskies.server.databases.mongo.main.models.FormattedChatDC
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getChats(
    isCheckToken: Boolean = SettingRequests.isCheckToken
) = cRoute(
    SystemRouting.UserRoute.getChat,
    method = HttpMethod.Get,
    isCheckToken = isCheckToken
) { token ->

    val chats = getChats(token.idUser)
    call.respond(HttpStatusCode.OK, chats)
}

suspend fun getChats(idUser: String): List<FormattedChatDC> {

    val idsChats = MainDataBase.Users.getChatsByIdUser(idUser)
    val listChats = idsChats.map { id ->
        val chat = MainDataBase.Chats.getChatById(id)
        val companion = if (idUser == chat.firstCompanion?.idUser) chat.secondCompanion!! else chat.firstCompanion!!
        val imageComp = MainDataBase.Avatars.getAvatarByIdUser(companion.idUser)
        val lastMessage = MessagesDataBase.Messages.getLastMessage(id)
        var newMessagesMy = NewMessagesDataBase.NewMessages.getNewMessagesByIdChat(id, idUser)

        if (newMessagesMy.isEmpty()) {
            newMessagesMy = NewMessagesDataBase.NewMessages.getNewMessagesByIdChat(id, companion.idUser)
        }

        val username = MainDataBase.Users.getUsername(companion.idUser) ?: ""

        FormattedChatDC(
            id = chat.idChat,
            nameChat = username,
            idCompanion = companion.idUser,
            image = imageComp,
            lastMessage = if (newMessagesMy.isNotEmpty())
                newMessagesMy.last().message.ifEmpty { "Изображение" }
            else
                lastMessage
        )
    }
    return listChats
}