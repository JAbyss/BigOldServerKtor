package com.foggyskies.server.routes.content.requests

import com.foggyskies.server.data.model.ContentRequestDC
import com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.testpacage.content.collections.addNewContent
import com.foggyskies.server.databases.mongo.content.models.ContentPreviewDC
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import java.io.File
import java.util.*

fun Route.addPostImage(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ContentRoute.addPostImage,
    method = HttpMethod.Post,
    isCheckToken = isCheckToken
) { token ->

    val post = call.receive<ContentRequestDC>()

    addNewContent(post, callback = {
        call.respond(HttpStatusCode.OK, it)
    })
}

private suspend fun addNewContent(item: ContentRequestDC, callback: suspend (ContentPreviewDC) -> Unit) {
//    val decodedString = Base64.getDecoder().decode(item.item.value)
//    val id = ObjectId().toString()
//    val nameFile = "image_content_$id.jpg"
//    val pathFile = SystemRouting.Images.BASE_DIR + "/" + nameFile
//    File(pathFile).writeBytes(decodedString)
    val newPostReady = item.item.toNewPost()
    callback(newPostReady.toContentPreview())
    com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.addNewContent(item.idPageProfile, newPostReady)
}