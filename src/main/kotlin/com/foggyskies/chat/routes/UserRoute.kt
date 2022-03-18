package com.foggyskies.chat.routes

import com.foggyskies.ChatSession
import com.foggyskies.chat.data.UserNameID
import com.foggyskies.chat.extendfun.isTrue
import com.foggyskies.chat.room.UserRoomController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
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

        if (isTokenExist) {
            val session = call.sessions.get<ChatSession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                return@webSocket
            }
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        if (frame.readText().length >= 3) {
                            val users = roomUserController.getUsersByUsername(frame.readText())
                            if (users.isNotEmpty()) {
                                val parsedString = Json.encodeToString(users)
                                send(parsedString)
                            } else {
                                send("[]")
                            }
                        } else {
                            send("[]")
                        }
                    }
                }
            } catch (e: Exception) {
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

        if (isTokenExist) {
            val users = roomUserController.getUsers()
            call.respond(users)
        } else {
            call.respond(HttpStatusCode.NotFound, "Токен не был найден.")
        }
    }

    get("/chats") {

        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = roomUserController.checkOnExistToken(token.toString())

            if (isTokenExist) {
                val chats = roomUserController.getChats(token.toString())
                call.respond(chats)
            } else {
                call.respond(HttpStatusCode.NotFound, "Токен не был найден.")
            }
        }
    }

    post("/addFriend{idUserReceiver}") {
        val idUserReceiver = call.parameters["idUserReceiver"]
//        val idUserReceiver = call.receive<idUserReceiver>()
            ?: call.respond(HttpStatusCode.BadRequest, "IdUser не получен.")

        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = roomUserController.checkOnExistToken(token.toString())

            if (isTokenExist) {

                val user = roomUserController.getUserByToken(token.toString())

                val userNameId = UserNameID(
                    id = user.idUser,
                    username = user.username
                )

                roomUserController.addRequestToFriend(userNameId, idUserReceiver.toString())
                call.respond(HttpStatusCode.OK)
            }
        }

    }

    post("/acceptRequestFriend{userID}{username}") {

            val userId = call.parameters["userID"] ?: call.respond(HttpStatusCode.BadRequest, "UserID не получен.")
            val username =
                call.parameters["username"] ?: call.respond(HttpStatusCode.BadRequest, "Username не получен.")

            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

            isTrue(token.toString().isNotEmpty()) {
                val isTokenExist = roomUserController.checkOnExistToken(token.toString())

                if (isTokenExist) {

                    val user = roomUserController.getUserByToken(token.toString())

                    val userReceiver = UserNameID(
                        id = user.idUser,
                        username = user.username
                    )

                    val userSender = UserNameID(
                        id = userId.toString(),
                        username = username.toString()
                    )

                    roomUserController.acceptRequestFriend(userReceiver, userSender)
                    call.respond(HttpStatusCode.OK)
                }

            }
    }
    get("/friends") {

    }
}

@kotlinx.serialization.Serializable
data class IdUserReceiver(
    val idUserReceiver: String
)