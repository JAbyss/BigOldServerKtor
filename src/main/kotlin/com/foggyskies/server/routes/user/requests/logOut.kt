package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.delTokenByTokenId
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.logOut(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.UserRoute.logOut,
    method = HttpMethod.Get,
    isCheckToken = isCheckToken
) {token ->
    MainDataBase.TokenCol.delTokenByTokenId(token.id)
    call.respond(status = HttpStatusCode.OK, "LogOut complete")
}