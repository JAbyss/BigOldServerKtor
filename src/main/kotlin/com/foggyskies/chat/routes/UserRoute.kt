package com.foggyskies.chat.routes

import com.foggyskies.ChatSession
import com.foggyskies.chat.room.UserRoomController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.usersRoutes(roomUserController: UserRoomController) {


    webSocket("/user") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
        val isTokenExist = roomUserController.checkOnExistToken(token.toString())

        if (isTokenExist){
            val session = call.sessions.get<ChatSession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                return@webSocket
            }
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        if (frame.readText().length == 3) {
                            val users = roomUserController.getUsersByUsername(frame.readText())
                            if (users.isNotEmpty()) {
                                val parsedString = Json.encodeToString(users)
                                send(parsedString)
                            }
                        }
                    }
                }
            } catch (e: Exception){
                println(e)
            }
        } else {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
            return@webSocket
        }
    }

    get("/users") {

        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        val isTokenExist = roomUserController.checkOnExistToken(token.toString())

        if(isTokenExist){
            val users = roomUserController.getUsers()
            call.respond(users)
        } else {
            call.respond(HttpStatusCode.NotFound, "Токен не был найден.")
        }
    }
}