package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.testpacage.Logger
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.checks
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.bson
import org.litote.kmongo.json
import org.litote.kmongo.util.tripleProjectionCodecRegistry

fun Route.getPagesProfile(
    isCheckToken: Boolean = SettingRequests.isCheckToken
) = cRoute(
    SystemRouting.UserRoute.getPagesProfile,
    method = HttpMethod.Get,
    isCheckToken = isCheckToken
) { token ->

    val listPages = getAllPagesByIdUser(token.idUser)

    call.respond(HttpStatusCode.OK, listPages)
}

fun params(vararg values: () -> Unit) {
    values.forEach {
        it()
    }
}