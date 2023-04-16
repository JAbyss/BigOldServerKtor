package com.foggyskies.server.routes.content.requests

import com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.testpacage.content.collections.getOnePostComments
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getUserByIdUser
import com.foggyskies.server.databases.mongo.main.models.UserIUSI
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.content.models.FormattedCommentDC
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getComments(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ContentRoute.getComments,
    method = HttpMethod.Get,
    isCheckToken
) {

    val idPageProfile =
        call.request.queryParameters["idPageProfile"] ?: return@cRoute call.respond(
            HttpStatusCode.BadRequest,
            "IdPageProfile не получен."
        )
    val idPost =
        call.request.queryParameters["idPost"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest, "IdPost не получен.")

    val comments = com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.getOnePostComments(idPageProfile, idPost)

    val idsAndUsername = hashMapOf<String, UserIUSI>()

    comments.forEach { comment ->
        if (!idsAndUsername.containsKey(comment.idUser)) {
            val user = MainDataBase.Users.getUserByIdUser(comment.idUser).toUserIUSI()
            val image = MainDataBase.Avatars.getAvatarByIdUser(comment.idUser)
            idsAndUsername[comment.idUser] = user.copy(image = image)
        }
    }

    val formattedCommentDC = FormattedCommentDC(
        users = idsAndUsername,
        comments = comments
    )

    call.respond(status = HttpStatusCode.OK, formattedCommentDC)
}

