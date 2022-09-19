package com.foggyskies.server.routes.content.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.content.collections.addNewComment
import com.foggyskies.server.databases.mongo.content.models.CommentDC
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.*

fun Route.addCommentToPost(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ContentRoute.addCommentToPost,
    method = HttpMethod.Post,
    isCheckToken = isCheckToken
) {
    val comment = call.receive<CommentDC>()

    val idPageProfile =
        call.parameters["idPageProfile"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest, "IdPageProfile не получен.")
    val idPost =
        call.parameters["idPost"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest, "IdPost не получен.")

    addNewComment(idPageProfile, idPost, comment)

    call.respond(HttpStatusCode.OK)
}

private suspend fun addNewComment(idPageProfile: String, idPost: String, comment: CommentDC){
    val sdf = SimpleDateFormat("hh:mm")
    val currentDate = sdf.format(Date())
    ContentDataBase.Content.addNewComment(
        idPageProfile,
        idPost,
        comment.copy(id = ObjectId().toString(), date = currentDate)
    )
}