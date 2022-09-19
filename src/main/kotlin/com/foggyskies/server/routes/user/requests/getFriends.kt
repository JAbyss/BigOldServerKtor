package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getFriendsByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getUserByIdUser
import com.foggyskies.server.databases.mongo.main.models.FriendListDC
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getFriends(
    isCheckToken: Boolean = SettingRequests.isCheckToken
) = cRoute(
    SystemRouting.UserRoute.getFriends,
    method = HttpMethod.Get,
    isCheckToken = isCheckToken
) { token ->

    val listFriends = getFriends(token.idUser)
    call.respond(HttpStatusCode.OK, listFriends)
}

private suspend fun getFriends(idUser: String): List<FriendListDC> {

    val friends =  MainDataBase.Friends.getFriendsByIdUser(idUser)
    val listFormattedFriends = friends.map { friend ->
        val user = MainDataBase.Users.getUserByIdUser(friend)
        val image = MainDataBase.Avatars.getAvatarByIdUser(friend)

        FriendListDC(
            id = user.idUser,
            username = user.username,
            status = user.status,
            image = image
        )
    }
    return listFormattedFriends
}