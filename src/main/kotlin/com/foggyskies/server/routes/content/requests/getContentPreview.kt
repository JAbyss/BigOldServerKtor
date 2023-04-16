package com.foggyskies.server.routes.content.requests

import com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.testpacage.content.collections.getFirstFiftyContentPreview
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getContentPreview(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.ContentRoute.getContentPreview,
    method = HttpMethod.Get,
    isCheckToken = isCheckToken
) { token ->

    val idPageProfile =
        call.request.queryParameters["idPageProfile"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest, "Id не получен.")

    val content = com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.Content.getFirstFiftyContentPreview(idPageProfile)

    call.respond(HttpStatusCode.OK, content)
}