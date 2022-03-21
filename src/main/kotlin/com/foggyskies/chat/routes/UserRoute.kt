package com.foggyskies.chat.routes

import com.foggyskies.ChatSession
import com.foggyskies.chat.data.model.UserNameID
import com.foggyskies.chat.extendfun.isTrue
import com.foggyskies.chat.newroom.UserRoutController
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
import org.koin.ktor.ext.inject

fun Route.usersRoutes() {

    val routController by inject<UserRoutController>()
    fun createMainSocket(idUser: String) {
        webSocket("/mainSocket/$idUser") {
            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
            val isTokenExist = routController.checkOnExistToken(token.toString())

            val map_actions = mapOf(
                "getFriends" to suspend { "getFriends${Json.encodeToString(routController.getFriends(token.toString()))}" },
                "getChats" to suspend { "getFriends${Json.encodeToString(routController.getChats(token.toString()))}" },
                "getRequestsFriends" to suspend {
                    "getRequestsFriends${
                        Json.encodeToString(
                            routController.getRequestsFriends(
                                token.toString()
                            )
                        )
                    }"
                },
            )
            val map_action_unit = mapOf(
                "logOut" to suspend { routController.logOut(token.toString()) },
//            "acceptRequestFriend" to suspend { roomUserController.acceptRequestFriend() }
            )


            val user = routController.getUserByToken(token.toString())

            if (isTokenExist) {
                val session = call.sessions.get<ChatSession>()
                if (session == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                    return@webSocket
                }
                try {
                    async {
                        routController.watchForFriend(idUser, this@webSocket)
                    }
                    async {
                        routController.watchForRequestsFriends(idUser, this@webSocket)
                    }

                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val incomingData = frame.readText()
                            val regex = ".+(?=\\|)".toRegex()
                            val action = regex.find(incomingData)?.value

                            if (map_actions.containsKey(action)) {
                                val friends = map_actions[action]?.invoke()
                                if (friends != null) {
                                    send(friends)
                                }
                            } else if (map_action_unit.containsKey(action)){
                                map_action_unit[action]?.invoke()
                            } else if (action == "acceptRequestFriend"){
                                val idUserReceiver = incomingData.replace("$action|", "")
                                val userSender = UserNameID(
                                    id = user.idUser,
                                    username = user.username
                                )
                                routController.acceptRequestFriend(userSender, idUserReceiver)
                            } else if (action == "addFriend"){
                                val idReceiver = incomingData.replace("$action|", "")
                                routController.addRequestToFriend(UserNameID(id = user.idUser, username = user.username), idReceiver)
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
    }
    post("/createMainSocket") {
//        val idUser = call.parameters["idUser"]

        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val user = routController.getUserByToken(token.toString())
            createMainSocket(user.idUser)
            call.respond(HttpStatusCode.OK, user.idUser)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Token не существует.")
        }
    }



    webSocket("/user") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
        val isTokenExist = routController.checkOnExistToken(token.toString())

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
                            val users = routController.searchUsers(frame.readText())
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

//    get("/users") {
//
//        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//
//        val isTokenExist = routController.checkOnExistToken(token.toString())
//
//        if (isTokenExist) {
//            val users = routController.getUsers()
//            call.respond(users)
//        } else {
//            call.respond(HttpStatusCode.NotFound, "Токен не был найден.")
//        }
//    }

    get("/chats") {

        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = routController.checkOnExistToken(token.toString())

            if (isTokenExist) {
                val chats = routController.getChats(token.toString())
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
            val isTokenExist = routController.checkOnExistToken(token.toString())

            if (isTokenExist) {

                val user = routController.getUserByToken(token.toString())

                val userNameId = UserNameID(
                    id = user.idUser,
                    username = user.username
                )

                routController.addRequestToFriend(userNameId, idUserReceiver.id)
                call.respond(HttpStatusCode.OK)
            }
        }

    }

//    post("/acceptRequestFriend{userID}{username}") {
//
//        val userId = call.parameters["userID"] ?: call.respond(HttpStatusCode.BadRequest, "UserID не получен.")
//        val username =
//            call.parameters["username"] ?: call.respond(HttpStatusCode.BadRequest, "Username не получен.")
//
//        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//
//        isTrue(token.toString().isNotEmpty()) {
//            val isTokenExist = roomUserController.checkOnExistToken(token.toString())
//
//            if (isTokenExist) {
//
//                val user = roomUserController.getUserByToken(token.toString())
//
//                val userReceiver = UserNameID(
//                    id = user.idUser,
//                    username = user.username
//                )
//
//                val userSender = UserNameID(
//                    id = userId.toString(),
//                    username = username.toString()
//                )
//
//                roomUserController.acceptRequestFriend(userReceiver, userSender)
//                call.respond(HttpStatusCode.OK)
//            }
//
//        }
//    }
    get("/friends") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = routController.checkOnExistToken(token.toString())

            if (isTokenExist) {
                val listFriends = routController.getFriends(token.toString())
                call.respond(HttpStatusCode.OK, listFriends)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Токен неверный.")
            }
        }
    }
//    post {
//
//    }
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