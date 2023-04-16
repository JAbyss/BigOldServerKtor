package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getRequestsFriendByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getUserByIdUser
import com.foggyskies.server.databases.mongo.main.models.UserIUSI
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getRequestsFriends(
    isCheckToken: Boolean
) = cRoute(
    path = SystemRouting.UserRoute.getRequestsFriends,
    method = HttpMethod.Get,
    isCheckToken = isCheckToken
) { token ->

    val request = getRequestsFriends(token.idUser)

    call.respond(status = HttpStatusCode.OK, request)
}

suspend fun getRequestsFriends(idUser: String): List<UserIUSI> {
    val request = MainDataBase.Requests.getRequestsFriendByIdUser(idUser)
    val listFormattedRequests = request.map { user ->

        val fullUser = MainDataBase.Users.getUserByIdUser(user)
        val avatar = MainDataBase.Avatars.getAvatarByIdUser(user)

        UserIUSI(
            id = fullUser.idUser,
            username = fullUser.username,
            status = fullUser.status,
            image = avatar
        )
    }
    return listFormattedRequests
}