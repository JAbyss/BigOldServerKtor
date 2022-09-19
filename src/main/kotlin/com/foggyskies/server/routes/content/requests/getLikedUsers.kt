package com.foggyskies.server.routes.content.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.content.collections.getAllLikedUsers
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getLikedUsers(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ContentRoute.getLikedUsers,
    method = HttpMethod.Get,
    isCheckToken
) { token ->

    val idPageProfile =
        call.parameters["idPageProfile"] ?: return@cRoute call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "IdPageProfile не получен."
        )
    val idPost = call.parameters["idPost"] ?: return@cRoute call.respondText(
        status = HttpStatusCode.BadRequest,
        text = "IdPost не получен."
    )

    val likedUsers = ContentDataBase.Content.getAllLikedUsers(idPageProfile, idPost)
    call.respond(likedUsers)
}