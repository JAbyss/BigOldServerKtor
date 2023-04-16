package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.testpacage.content.collections.deleteCollection
import com.foggyskies.server.databases.mongo.testpacage.content.collections.getAllAddressesContent
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.deletePage
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarPageProfile
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.deletePageProfile(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.UserRoute.deletePageProfile,
    method = HttpMethod.Delete,
    isCheckToken
) {

    val idPageProfile = call.request.headers["IdPageProfile"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest)

    val content = com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.getAllAddressesContent(idPageProfile)

    val regex = ".+(?>$idPageProfile)".toRegex()
    val path = content[0]
    val dirForDelete = regex.find(path)?.value
    dirForDelete?.let { File(it).delete() }

    com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.deleteCollection(idPageProfile)

    val pathAvatar = MainDataBase.PagesProfile.getAvatarPageProfile(idPageProfile)
    val dirAvatars = regex.find(pathAvatar)?.value

    dirAvatars?.let { File(it).delete() }

    MainDataBase.PagesProfile.deletePage(idPageProfile)

    call.respond(HttpStatusCode.OK)
}