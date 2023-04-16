package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.*
import com.foggyskies.server.databases.mongo.main.models.FriendDC
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.acceptRequestFriend(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.UserRoute.acceptRequestFriend,
    method = HttpMethod.Post,
    isCheckToken
) { token ->

    val idUserSender = call.receiveText()

    acceptRequestFriend(token.idUser, idUserSender)
    call.respond(HttpStatusCode.OK)
}

private suspend fun acceptRequestFriend(userReceiver: String, idUserSender: String) {
    val requestsFriend = MainDataBase.Requests.getRequestsFriendByIdUser(userReceiver)

    if (requestsFriend.isNotEmpty()) {
        requestsFriend.forEach { userSenderL ->
            if (userSenderL == idUserSender) {

                val friendsReceiver = MainDataBase.Friends.getFriendsDocumentFriendByIdUser(userReceiver)
                val friendsSender = MainDataBase.Friends.getFriendsDocumentFriendByIdUser(idUserSender)

                if (friendsReceiver != null) {

                    MainDataBase.Friends.addFriendByIdUser(userReceiver, idUserSender)
                    MainDataBase.Requests.delRequestFriendsByIdUser(userReceiver, idUserSender)
                } else {
                    val document = FriendDC(
                        idUser = userReceiver,
                        friends = listOf(idUserSender)
                    )
                    MainDataBase.Friends.insertFriendByIdUser(document)
                    MainDataBase.Requests.delRequestFriendsByIdUser(userReceiver, idUserSender)
                }

                if (friendsSender != null) {
                    MainDataBase.Friends.addFriendByIdUser(idUserSender, userReceiver)
                } else {
                    val document = FriendDC(
                        idUser = idUserSender,
                        friends = listOf(userReceiver)
                    )
                    MainDataBase.Friends.insertFriendByIdUser(document)
                }
            }
        }
    }
}