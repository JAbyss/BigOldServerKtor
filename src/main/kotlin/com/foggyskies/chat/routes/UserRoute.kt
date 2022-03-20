package com.foggyskies.chat.routes

import com.foggyskies.ChatSession
import com.foggyskies.chat.data.model.FriendListDC
import com.foggyskies.chat.data.model.UserNameID
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
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.concurrent.thread
import kotlin.reflect.full.functions

fun Route.usersRoutes(roomUserController: UserRoomController) {

    fun createMainSocket(idUser: String) {
        webSocket("/mainSocket/$idUser") {
            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
            val isTokenExist = roomUserController.checkOnExistToken(token.toString())

            val map_actions = mapOf(
                "getFriends" to suspend { "getFriends${Json.encodeToString(roomUserController.getFriends(token.toString()))}" },
                "getChats" to suspend { "getFriends${Json.encodeToString(roomUserController.getChats(token.toString()))}" },
                "getRequestsFriends" to suspend { "getRequestsFriends${Json.encodeToString(roomUserController.getRequestsFriends(token.toString()))}" }
            )
//            val map_add_actions = mapOf(
//                "addFriend" to {}
//            )

            val user = roomUserController.getUserByToken(token.toString())

            if (isTokenExist) {
                val session = call.sessions.get<ChatSession>()
                if (session == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                    return@webSocket
                }
                try {
                    async {
                        roomUserController.watchForFriend(idUser, this@webSocket)
                    }
                    async {
                        roomUserController.watchForRequestsFriends(idUser, this@webSocket)
                    }
//                    Dispatchers.Unconfined

//                    CoroutineScope(Dispatchers.Unconfined).launch {  }
//                    CoroutineScope(Dispatchers.IO).launch {
//                    }
//                    roomUserController.watchForRequestsFriends(idUser, this)
//                    roomUserController.watchForRequestsFriends(idUser, this)
//                    roomUserController.watchForRequestsFriends(idUser, this)



                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val action = frame.readText()
//                            if (action.matches("^add".toRegex())) {
//                                val value = action.replace(action, "")
//                                when (action) {
//                                    "addFriend" -> {
//                                        roomUserController.addRequestToFriend(
//                                            userSender = UserNameID(
//                                                id = idUser,
//                                                username = user.username
//                                            ), idUserReceiver = value
//                                        )
//                                    }
//                                }
//                            }
                            val friends = map_actions[action]?.invoke()
//                            val string = Json.encodeToString(friends as List<FriendListDC>)
                            if (friends != null) {
                                send(friends)
                            }
//                            if (frame.readText().length >= 3) {
//                                val users = roomUserController.getUsersByUsername(frame.readText())
//                                if (users.isNotEmpty()) {
//                                    val parsedString = Json.encodeToString(users)
//                                    send(parsedString)
//                                } else {
//                                    send("[]")
//                                }
//                            } else {
//                                send("[]")
//                            }
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
    }
    post("/createMainSocket") {
//        val idUser = call.parameters["idUser"]

        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
        val isTokenExist = roomUserController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val user = roomUserController.getUserByToken(token.toString())
            createMainSocket(user.idUser)
            call.respond(HttpStatusCode.OK, user.idUser)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Token не существует.")
        }
    }



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

    post("/addFriend") {
//        val idUserReceiver = call.parameters["idUserReceiver"]
        val idUserReceiver: IdUserReceiver = (call.receive<IdUserReceiver>()
            ?: call.respond(HttpStatusCode.BadRequest, "IdUser не получен.")) as IdUserReceiver

        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = roomUserController.checkOnExistToken(token.toString())

            if (isTokenExist) {

                val user = roomUserController.getUserByToken(token.toString())

                val userNameId = UserNameID(
                    id = user.idUser,
                    username = user.username
                )

                roomUserController.addRequestToFriend(userNameId, idUserReceiver.id)
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
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = roomUserController.checkOnExistToken(token.toString())

            if (isTokenExist) {
                val listFriends = roomUserController.getFriends(token.toString())
                call.respond(HttpStatusCode.OK, listFriends)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Токен неверный.")
            }
        }
    }
//    get("/requestsFriends") {
//        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//
//        isTrue(token.toString().isNotEmpty()) {
//            val isTokenExist = roomUserController.checkOnExistToken(token.toString())
//
//            if (isTokenExist) {
//                val listRequestsFriends = roomUserController.getRequestsFriends(token.toString())
//                call.respond(HttpStatusCode.OK, listRequestsFriends)
//            } else {
//                call.respond(HttpStatusCode.BadRequest, "Токен неверный.")
//            }
//        }
//    }
}

@kotlinx.serialization.Serializable
data class IdUserReceiver(
    val id: String
)