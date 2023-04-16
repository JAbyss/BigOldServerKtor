package com.foggyskies.server.routes.content.requests

import com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.testpacage.content.collections.addLikeToPost
import com.foggyskies.server.databases.mongo.testpacage.content.collections.delLikeToPost
import com.foggyskies.server.databases.mongo.testpacage.content.collections.getInfoAboutOnePost
import com.foggyskies.server.databases.mongo.content.models.IdPageAndPost
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addLikeToPost(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ContentRoute.addLikeToPost,
    method = HttpMethod.Post,
    isCheckToken
) { token ->

    val data = call.receive<IdPageAndPost>()

//    val idPageProfile =
//        call.parameters["idPageProfile"] ?: return@cRoute call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "IdPageProfile не получен."
//        )
//    val idPost = call.parameters["idPost"] ?: return@cRoute call.respondText(
//        status = HttpStatusCode.BadRequest,
//        text = "IdPost не получен."
//    )

    val isLiked = addLikeToPost(data.idPageProfile, data.idPost, token.idUser)
    call.respond(HttpStatusCode.OK, isLiked)
}

suspend fun addLikeToPost(idPageProfile: String, idPost: String, idUser: String): Boolean {

    val likesList = com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.getInfoAboutOnePost(idPageProfile, idPost)?.likes ?: emptyList()
    return if (!likesList.contains(idUser)) {
        com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.addLikeToPost(idPageProfile, idPost, idUser)
        true
    } else {
        com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.delLikeToPost(idPageProfile, idPost, idUser)
        false
    }
}