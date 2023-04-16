package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getChatById
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.muteChat
import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity_
import com.foggyskies.server.databases.mongo.main.models.MuteChatDC
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.muteChat(isCheckToken: Boolean = SettingRequests.isCheckToken) = cRoute(
    SystemRouting.UserRoute.muteChat,
    method = HttpMethod.Post,
    isCheckToken = isCheckToken
) { token ->
    val mutedChat = call.receive<MuteChatDC>() ?: return@cRoute call.respond(status = HttpStatusCode.BadRequest, "")

    muteChat(mutedChat, token.idUser)
    call.respond(HttpStatusCode.OK)
}

suspend fun muteChat(mutedChat: MuteChatDC, idUser: String) {
    val chat = MainDataBase.Chats.getChatById(mutedChat.idChat)
    val nameField =
        if (chat.firstCompanion?.idUser == idUser) ChatMainEntity_.FirstCompanion else ChatMainEntity_.SecondCompanion
    MainDataBase.Chats.muteChat(mutedChat.idChat, idUser, nameField, mutedChat.timeMute)
}