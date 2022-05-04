package com.foggyskies.chat.routes

import com.foggyskies.ChatSession
import com.foggyskies.chat.data.model.PageProfileDC
import com.foggyskies.chat.data.model.UserNameID
import com.foggyskies.chat.extendfun.isTrue
import com.foggyskies.chat.newroom.UserRoutController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

private var sessionsMainSockets = ConcurrentList<String>()

fun Route.usersRoutes() {

    val routController by inject<UserRoutController>()
    fun createMainSocket(idUser: String) {
        if (!sessionsMainSockets.contains(idUser)) {
            sessionsMainSockets.add(idUser)
            webSocket("/mainSocket/$idUser") {

                val session = call.sessions.get<ChatSession>()
                if (session == null) {
                    routController.setStatusUser(idUser, "Не в сети")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//                    sessionsMainSockets.remove(idUser)
                    return@webSocket
                }

                val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
                val isTokenExist = routController.checkOnExistToken(token.toString())

                val map_actions = mapOf(
                    "getFriends" to suspend { "getFriends|${Json.encodeToString(routController.getFriends(token.toString()))}" },
                    "getChats" to suspend { "getFriends|${Json.encodeToString(routController.getChats(token.toString()))}" },
                    "getRequestsFriends" to suspend {
                        "getRequestsFriends|${
                            Json.encodeToString(
                                routController.getRequestsFriends(
                                    token.toString()
                                )
                            )
                        }"
                    },
                    "getChats" to suspend {
                        "getChats|${Json.encodeToString(routController.getChats(token.toString()))}"
                    },
                    "getPagesProfile" to suspend {
                        "getPagesProfile|${
                            Json.encodeToString(
                                routController.getAllPagesByIdUser(
                                    idUser
                                )
                            )
                        }"
                    }
                )
                val map_action_unit = mapOf(
                    "logOut" to suspend { routController.logOut(token.toString()) },
                    "deleteAllSentNotifications" to suspend { routController.deleteAllSentNotifications(idUser) }
//            "acceptRequestFriend" to suspend { roomUserController.acceptRequestFriend() }
                )



                if (isTokenExist) {
                    routController.setStatusUser(idUser, "В сети")
                    val user = routController.getUserByToken(token.toString())

//                    try {
                    val watcherFriends = async {
                        routController.watchForFriend(idUser, this@webSocket)
                    }
                    val watcherRequestsFriends = async {
                        routController.watchForRequestsFriends(idUser, this@webSocket)
                    }
                    //fixme На время выключено
//                    val watcherInternalNotifications = async {
//                        routController.watchForInternalNotifications(idUser, this@webSocket)
//                    }


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
                            } else if (map_action_unit.containsKey(action)) {
                                map_action_unit[action]?.invoke()
                            } else if (action == "acceptRequestFriend") {
                                val idUserReceiver = incomingData.replace("$action|", "")
                                val userSender = UserNameID(
                                    id = user.idUser,
                                    username = user.username
                                )
                                routController.acceptRequestFriend(userSender, idUserReceiver)
                            } else if (action == "addFriend") {
                                val idReceiver = incomingData.replace("$action|", "")
                                routController.addRequestToFriend(
                                    UserNameID(
                                        id = user.idUser,
                                        username = user.username
                                    ), idReceiver
                                )
                            }
                        }
                    }
//                    } catch (e: Exception) {
//                        println(e)
//                    } finally {
                    routController.setStatusUser(idUser, "Не в сети")
                    watcherFriends.cancel()
                    watcherRequestsFriends.cancel()
//                    watcherInternalNotifications.cancel()
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
//                    sessionsMainSockets.remove(idUser)
                    return@webSocket
//                    }
                } else {
                    routController.setStatusUser(idUser, "Не в сети")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
//                    sessionsMainSockets.remove(idUser)
                    return@webSocket
                }
            }
        }
    }

    static("/") {
        files(".")
    }

//    get("/images/{name_image}") {
//        val name_image = call.parameters["name_image"] ?: call.respond(HttpStatusCode.BadRequest, "Картинка не указана")
//
//        val file = File("images/$name_image")
//        call.respondFile(file)
//    }

    post("/createPageProfile") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
        val isTokenExist = routController.checkOnExistToken(token.toString())

        val page = call.receive<PageProfileDC>()

        if (isTokenExist) {

            val decodedString = Base64.getDecoder().decode(page.image)
            val countFiles = File("images/").list().size + 1
            File("images/image_profile_$countFiles.png").writeBytes(decodedString)

            val user = routController.getUserByToken(token.toString())

            routController.addOnePage(
                user.idUser,
                page.copy(id = ObjectId().toString(), image = "image_profile_$countFiles.png")
            )

            call.respond(HttpStatusCode.OK, "Страница создана")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Token не существует.")
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
            val user = routController.getUserByToken(token.toString())

            val session = call.sessions.get<ChatSession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                return@webSocket
            }
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        if (frame.readText().length >= 3) {
                            val users = routController.searchUsers(idUser = user.idUser, frame.readText())
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

    get("/avatar") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val idUser = routController.getUserByToken(token.toString()).idUser
            val avatar = routController.getAvatarByIdUser(idUser)
            call.respond(HttpStatusCode.OK, avatar)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Токен не существует.")
        }
    }

    post ("/changeAvatar") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

//        val image = call.parameters["image"] ?: call.respond(HttpStatusCode.BadRequest, "Image не получен.")

        val image = call.receiveText()

        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val idUser = routController.getUserByToken(token.toString()).idUser

            val avatarOld = routController.getAvatarByIdUser(idUser)

            if (avatarOld.isNotEmpty()) {
                routController.deleteAvatarByIdUser(idUser, avatarOld)
            }

            val decodedString = Base64.getDecoder().decode(image.toString())
            val countFiles = File("images/avatars/").list().size + 1
            val name = ObjectId().toString()
            File("images/avatars/avatar_${name}.png").writeBytes(decodedString)
            val avatar = routController.changeAvatarByUserId(idUser, "images/avatars/avatar_$name.png")
            call.respond(HttpStatusCode.OK, avatar)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Токен не существует.")
        }
    }
    get("/getPagesProfileByIdUser{idUser}") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        val idOtherUser = call.parameters["idUser"] ?: call.respond(HttpStatusCode.BadRequest, "IdUser не получен.")

        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
//            val otherUser = routController.getUserByIdUser(idOtherUser.toString())
            val listPages = routController.getAllPagesByIdUser(idOtherUser.toString())
            call.respond(HttpStatusCode.OK, listPages)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Токен не существует.")
        }
    }
}

@kotlinx.serialization.Serializable
data class IdUserReceiver(
    val id: String
)