package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.addRequestFriendsByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getUserByIdUser
import com.foggyskies.server.databases.mongo.main.models.UserMainEntity
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addFiend(
    isCheckToken: Boolean = SettingRequests.isCheckToken
) = cRoute(
    SystemRouting.UserRoute.addFriend,
    method = HttpMethod.Post,
    isCheckToken = isCheckToken
) { token ->
    val idUserReceiver = call.receiveText()

    sendRequestFriend(token.idUser, idUserReceiver)

    call.respond(HttpStatusCode.OK)
}

private suspend fun sendRequestFriend(user: String, idUserReceiver: String) {
    MainDataBase.Requests.addRequestFriendsByIdUser(idUserReceiver, user)
}
//TODO убрать
//suspend inline fun getUserByToken(token: String): UserMainEntity {
//    val idUser = MainDataBase.TokenCol.getTokenByToken(token).idUser
//    return MainDataBase.Users.getUserByIdUser(idUser)
//}

suspend inline fun getUserByIdUser(idUser: String): UserMainEntity {
    return MainDataBase.Users.getUserByIdUser(idUser)
}