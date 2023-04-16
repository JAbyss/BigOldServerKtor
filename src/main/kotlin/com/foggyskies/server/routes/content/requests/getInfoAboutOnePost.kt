package com.foggyskies.server.routes.content.requests

import KeyPost
import SelectedPostWithIdPageProfile
import SystemDoc
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.testpacage.content.collections.getInfoAboutOnePost
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarByIdUser
import com.foggyskies.server.databases.mongo.content.models.IdPageAndPost
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.user.requests.getUserByIdUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getInfoAboutOnePost(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ContentRoute.getInfoAboutOnePost,
    method = HttpMethod.Post,
    isCheckToken
) { token ->

    val data = call.receive<IdPageAndPost>()

    val info = getInfoAboutOnePost(data.idPageProfile, data.idPost, token.idUser)
    call.respond(HttpStatusCode.OK, info ?: Unit)
}

suspend fun getInfoAboutOnePost(
    idPageProfile: String,
    idPost: String,
    idUser: String
): SelectedPostWithIdPageProfile? {
    val systemDoc =
        com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.db.getCollection<SystemDoc>("content_$idPageProfile").findOne("{_id: {\$eq: 'system'}}")
    val keyPost = KeyPost(idPage = idPageProfile)
    systemDoc?.let {
        keyPost.username = getUserByIdUser(it.owner_id).username
        keyPost.avatar = MainDataBase.Avatars.getAvatarByIdUser(it.owner_id)
    }

    return com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.getInfoAboutOnePost(idPageProfile, idPost)
        ?.toSelectedPostWithIdPageProfile(idPageProfile, idUser, keyPost.avatar, keyPost.username)
}