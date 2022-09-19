package com.foggyskies.server.routes.content.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.content.collections.getInfoAboutOnePost
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getPosts(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ContentRoute.getPosts,
    method = HttpMethod.Get,
    isCheckToken
) { token ->

    val posts = listOf(
        ContentDataBase.Content.getInfoAboutOnePost(
            idPageProfile = "629256b71372bb3eb6256391",
            idPost = "629257e01372bb3eb6256394"
        )?.apply {
            description = if (description == "Описание публикации...") "" else description
        },
        ContentDataBase.Content.getInfoAboutOnePost(
            idPageProfile = "629256b71372bb3eb6256391",
            idPost = "62925d3b1372bb3eb6256395"
        )?.apply {
            description = if (description == "Описание публикации...") "" else description
        },
        ContentDataBase.Content.getInfoAboutOnePost(
            idPageProfile = "62914107cc47483b16951b0b",
            idPost = "62accc9120936e5fbab1ca66"
        )?.apply {
            description = if (description == "Описание публикации...") "" else description
        })

    call.respond(HttpStatusCode.OK, posts)
}