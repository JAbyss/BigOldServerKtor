package com.foggyskies.server.plugin.verifications

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getIfExistToken
import com.foggyskies.server.databases.mongo.main.models.Token
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.checkToken(): Token? {

    return if (call.request.headers["Auth"] == null) {
        call.respond(status = HttpStatusCode.BadRequest, "Токен не получен")
        null
    } else {
        val token = call.request.headers["Auth"]!!

        val isTokenExist = MainDataBase.TokenCol.getIfExistToken(token)

        if (isTokenExist != null) {
            isTokenExist
        } else {
            call.respond(status = HttpStatusCode.BadRequest, "Токен неверный")
            null
        }
    }
}

suspend fun DefaultWebSocketServerSession.checkToken(): Token? {

    return if (call.request.headers["Auth"] == null) {
        call.respond(status = HttpStatusCode.BadRequest, "Токен не получен")
        null
    } else {
        val token = call.request.headers["Auth"]!!

        val isTokenExist = MainDataBase.TokenCol.getIfExistToken(token)

        if (isTokenExist != null) {
            isTokenExist
        } else {
            call.respond(status = HttpStatusCode.BadRequest, "Токен неверный")
            null
        }
    }
}