package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.data.model.ChatUserEntity
import com.foggyskies.server.data.model.CreateChat
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.addChatToUsersByIdUsers
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.checkOnExistChatByIdUsers
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.collections.createCollection
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.user.requests.getUserByIdUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Route.createChat(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ChatRoute.createChat,
    method = HttpMethod.Post,
    isCheckToken
) { token ->
    val secondUser = call.receiveText()

    val userFromToken = getUserByIdUser(token.idUser)

    val idChat = MainDataBase.Chats.checkOnExistChatByIdUsers(userFromToken.idUser, secondUser)

    idChat ?: run {
        val newIdChat = addNewChat(
            idUserFirst = userFromToken.idUser,
            idUserSecond = secondUser
        )
        return@cRoute call.respondText(status = HttpStatusCode.Created, text = newIdChat)
    }

    call.respondText(status = HttpStatusCode.OK, text = idChat)
}

private suspend fun addNewChat(idUserFirst: String, idUserSecond: String): String {
    val idChat = createChat(idUserFirst, idUserSecond)
    MainDataBase.Users.addChatToUsersByIdUsers(idUserFirst, idUserSecond, idChat)
    MessagesDataBase.Messages.createCollection(idChat)
    return idChat
}

private suspend fun createChat(firstCompanionId: String, secondCompanionId: String): String {
    val document = ChatMainEntity(
        idChat = ObjectId().toString(),
        firstCompanion = ChatUserEntity(idUser = firstCompanionId),
        secondCompanion = ChatUserEntity(idUser = secondCompanionId)
    )
    MainDataBase.Chats.getCollection().insertOne(document)
    return document.idChat
}