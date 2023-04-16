package com.foggyskies.server.plugin.verifications

import com.foggyskies.server.databases.mongo.main.models.Token
import com.foggyskies.server.plugin.SystemRouting.AuthServerURL.CheckTokenPath
import com.foggyskies.server.utils.KtorClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*

//suspend fun PipelineContext<Unit, ApplicationCall>.checkToken(): Token? {
//
//    return if (call.request.headers["Auth"] == null) {
//        call.respond(status = HttpStatusCode.BadRequest, "Токен не получен")
//        null
//    } else {
//        val token = call.request.headers["Auth"]!!
//
//        val isTokenExist = MainDataBase.TokenCol.getIfExistToken(token)
//
//        if (isTokenExist != null) {
//            isTokenExist
//        } else {
//            call.respond(status = HttpStatusCode.BadRequest, "Токен неверный")
//            null
//        }
//    }
//}

suspend fun PipelineContext<Unit, ApplicationCall>.checkToken(): Token {

    return KtorClient.use {
        val token = call.request.headers["Authorization"]
        if (token == null) {
            call.respond(status = HttpStatusCode.BadRequest, "Токен не получен")
            throw Exception("Токен не получен")
        } else {

            val response = it.post(CheckTokenPath) {
                header("Authorization", "Bearer $token")
            }
            return@use if (response.status.isSuccess())
                Token(token, response.bodyAsText())
            else
                throw Exception(response.status.description)
        }
    }

//    return if (call.request.headers["Auth"] == null) {
//        call.respond(status = HttpStatusCode.BadRequest, "Токен не получен")
//        null
//    } else {
//        val token = call.request.headers["Auth"]!!
//
//        val isTokenExist = MainDataBase.TokenCol.getIfExistToken(token)
//
//        if (isTokenExist != null) {
//            isTokenExist
//        } else {
//            call.respond(status = HttpStatusCode.BadRequest, "Токен неверный")
//            null
//        }
//    }
}

suspend fun DefaultWebSocketServerSession.checkToken(): Token {

    return KtorClient.use {
        val token = call.request.headers["Authorization"]
        if (token == null) {
            call.respond(status = HttpStatusCode.BadRequest, "Токен не получен")
            throw Exception("Токен не получен")
        } else {

            val response = it.post(CheckTokenPath) {
                header("Authorization", "Bearer $token")
            }
            return@use if (response.status.isSuccess())
                Token(token, response.bodyAsText())
            else
                throw Exception(response.status.description)
        }
    }
}

//suspend fun DefaultWebSocketServerSession.checkToken(): Token? {
//
//    return if (call.request.headers["Auth"] == null) {
//        call.respond(status = HttpStatusCode.BadRequest, "Токен не получен")
//        null
//    } else {
//        val token = call.request.headers["Auth"]!!
//
//        val isTokenExist = MainDataBase.TokenCol.getIfExistToken(token)
//
//        if (isTokenExist != null) {
//            isTokenExist
//        } else {
//            call.respond(status = HttpStatusCode.BadRequest, "Токен неверный")
//            null
//        }
//    }
//}